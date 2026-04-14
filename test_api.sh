#!/bin/bash
BASE_URL="http://localhost:10014"

echo "=========================================="
echo "图书借阅管理系统 API 测试报告"
echo "=========================================="
echo ""

echo "=== 测试1: 获取图书列表 ==="
curl -s "$BASE_URL/api/books" | python3 -m json.tool
echo ""

echo "=== 测试2: 图书搜索 (关键词: Java) ==="
curl -s "$BASE_URL/api/books?keyword=Java" | python3 -m json.tool
echo ""

echo "=== 测试3: 获取单本图书 ==="
curl -s "$BASE_URL/api/books/1" | python3 -m json.tool
echo ""

echo "=== 测试4: 借阅图书 ==="
curl -s -X POST "$BASE_URL/api/borrows/borrow" \
  -H "Content-Type: application/json" \
  -d '{"bookId":1,"borrowerName":"testuser","borrowerPhone":"13800138000","days":30}' | python3 -m json.tool
echo ""

echo "=== 测试5: 检查库存是否减少 ==="
curl -s "$BASE_URL/api/books/1" | python3 -m json.tool
echo ""

echo "=== 测试6: 重复借阅测试 (应该失败) ==="
curl -s -X POST "$BASE_URL/api/borrows/borrow" \
  -H "Content-Type: application/json" \
  -d '{"bookId":1,"borrowerName":"testuser","borrowerPhone":"13800138000","days":30}' | python3 -m json.tool
echo ""

echo "=== 测试7: 获取借阅记录列表 ==="
curl -s "$BASE_URL/api/borrows?keyword=testuser" | python3 -m json.tool
echo ""

echo "=== 测试8: 归还图书 ==="
RECORD_ID=$(curl -s "$BASE_URL/api/borrows?keyword=testuser" | python3 -c "import sys,json; d=json.load(sys.stdin); print(d['data'][0]['id'] if d['data'] else '')" 2>/dev/null)
if [ -n "$RECORD_ID" ]; then
  echo "归还记录ID: $RECORD_ID"
  curl -s -X POST "$BASE_URL/api/borrows/return/$RECORD_ID" | python3 -m json.tool
else
  echo "未找到借阅记录"
fi
echo ""

echo "=== 测试9: 检查库存是否恢复 ==="
curl -s "$BASE_URL/api/books/1" | python3 -m json.tool
echo ""

echo "=== 测试10: 添加新图书 ==="
curl -s -X POST "$BASE_URL/api/books" \
  -H "Content-Type: application/json" \
  -d '{"name":"测试图书","author":"测试作者","isbn":"978-0-000-00000-0","category":"测试","totalStock":2}' | python3 -m json.tool
echo ""

echo "=== 测试11: 更新图书 ==="
curl -s -X PUT "$BASE_URL/api/books/1" \
  -H "Content-Type: application/json" \
  -d '{"name":"Java编程思想(第4版)","author":"Bruce Eckel","isbn":"978-7-111-21382-6","category":"编程","totalStock":10,"availableStock":10}' | python3 -m json.tool
echo ""

echo "=== 测试12: 获取超期记录 ==="
curl -s "$BASE_URL/api/borrows/overdue" | python3 -m json.tool
echo ""

echo "=== 测试13: 删除图书测试 ==="
NEW_BOOK_ID=$(curl -s "$BASE_URL/api/books?keyword=测试图书" | python3 -c "import sys,json; d=json.load(sys.stdin); print(d['data'][0]['id'] if d['data'] else '')" 2>/dev/null)
if [ -n "$NEW_BOOK_ID" ]; then
  echo "删除图书ID: $NEW_BOOK_ID"
  curl -s -X DELETE "$BASE_URL/api/books/$NEW_BOOK_ID" | python3 -m json.tool
else
  echo "未找到测试图书"
fi
echo ""

echo "=== 测试14: 跨域测试 (OPTIONS请求) ==="
curl -s -X OPTIONS "$BASE_URL/api/books" \
  -H "Origin: http://localhost:5173" \
  -H "Access-Control-Request-Method: GET" \
  -H "Access-Control-Request-Headers: Content-Type" -v 2>&1 | grep -E "Access-Control|HTTP"
echo ""

echo "=========================================="
echo "测试完成"
echo "=========================================="
