#!/bin/bash
APP_NAME=library-system-10013
APP_JAR=/opt/library-system-10013/library-system-10013.jar
LOG_FILE=/opt/library-system-10013/logs-10013.log
DATA_DIR=/opt/library-system-10013/data-10013
PID_FILE=/opt/library-system-10013/app-10013.pid

# 创建数据目录
mkdir -p $DATA_DIR

# 停止旧进程
if [ -f $PID_FILE ]; then
    OLD_PID=$(cat $PID_FILE)
    if ps -p $OLD_PID > /dev/null 2>&1; then
        kill -9 $OLD_PID 2>/dev/null
    fi
    rm -f $PID_FILE
fi

# 启动应用
nohup java -jar $APP_JAR \
    --server.port=10013 \
    --library.data.path=$DATA_DIR \
    > $LOG_FILE 2>&1 &

NEW_PID=$!
echo $NEW_PID > $PID_FILE
echo "应用已启动，PID: $NEW_PID，端口: 10013"
echo "日志文件: $LOG_FILE"
