#!/bin/bash

LOCK_FILE="/tmp/monitor_localstack_running"
SERVICE_NAME="localstack"

# 既にスクリプトが動いているかチェック
if [ -f "$LOCK_FILE" ]; then
  echo "monitor_localstack.sh は既に実行中です。"
  exit 0
fi

# 実行中フラグファイルを作成
touch "$LOCK_FILE"

# 無限ループでログを監視
while true; do
  # コンテナのログの末尾から新しいログを監視
  docker compose logs --tail 100 -f $SERVICE_NAME 2>&1 | while read line; do
    if echo "$line" | grep -q "An error occurred (ResourceNotFoundException) when calling the GetShardIterator operation: Unable to find record with provided SequenceNumber SequenceNumber(0) in stream Some"; then
      echo "エラー検出: LocalStack を再起動します..."
      docker compose down -v $SERVICE_NAME
      docker compose up -d $SERVICE_NAME
      break  # 再起動後にログ監視を終了して新たに監視を始める
    fi
  done
  sleep 5  # 再起動後少し待ってから新たに監視を開始
done

# スクリプトが終了したらフラグファイルを削除
rm -f "$LOCK_FILE"
