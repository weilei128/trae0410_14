#!/bin/bash
set -e

echo "=== 图书管理系统部署脚本 ==="
echo "端口: 后端=10011, 前端=10021"
echo ""

BACKEND_PORT=10011
FRONTEND_PORT=10021
PROJECT_DIR="/opt/library-system-10011"

echo "1. 关闭旧进程"
echo "-------------------------"
PID=$(lsof -ti:$BACKEND_PORT 2>/dev/null || echo "")
if [ ! -z "$PID" ]; then
    echo "停止后端端口 $BACKEND_PORT 进程 PID: $PID"
    kill -9 $PID 2>/dev/null || true
    sleep 2
fi
echo "后端端口已清理"

echo ""
echo "2. 创建目录"
echo "-------------------------"
mkdir -p $PROJECT_DIR
mkdir -p $PROJECT_DIR/data
mkdir -p $PROJECT_DIR/logs
mkdir -p $PROJECT_DIR/dist-10021
echo "目录创建完成"

echo ""
echo "3. 检查环境"
echo "-------------------------"
which java && java -version 2>&1 | head -1
which mvn && mvn -version 2>&1 | head -1
which node && node -v
which nginx && nginx -v 2>&1

echo ""
echo "部署环境准备完成"
