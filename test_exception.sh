#!/bin/bash
BASE_URL="http://localhost:10014"

echo "=========================================="
echo "异常场景测试"
echo "=========================================="
echo ""

echo "=== 异常测试1: 获取不存在的图书 ==="
curl -s "$BASE_URL/api/books/99999" | python3 -m json.tool
echo ""

echo "=== 异常测试2: 借阅不存在的图书 ==="
curl -s -X POST "$BASE_URL/api/borrows/borrow" \
  -H "Content-Type: application/json" \
  -d '{"bookId":99999,"borrowerName":"test","borrowerPhone":"13800000000","days":30}' | python3 -m json.tool
echo ""

echo "=== 异常测试3: 借阅图书-缺少必填字段 ==="
curl -s -X POST "$BASE_URL/api/borrows/borrow" \
  -H "Content-Type: application/json" \
  -d '{"bookId":1}' | python3 -m json.tool
echo ""

echo "=== 异常测试4: 借阅图书-库存为0 ==="
curl -s -X PUT "$BASE_URL/api/books/1" \
  -H "Content-Type: application/json" \
  -d '{"name":"测试库存0","author":"test","isbn":"000","category":"test","totalStock":0,"availableStock":0}' | python3 -m json.tool
curl -s -X POST "$BASE_URL/api/borrows/borrow" \
  -H "Content-Type: application/json" \
  -d '{"bookId":1,"borrowerName":"test","borrowerPhone":"13800000000","days":30}' | python3 -m json.tool
curl -s -X PUT "$BASE_URL/api/books/1" \
  -H "Content-Type: application/json" \
  -d '{"name":"Java编程思想(第4版)","author":"Bruce Eckel","isbn":"978-7-111-21382-6","category":"编程","totalStock":10,"availableStock":10}' | python3 -m json.tool
echo ""

echo "=== 异常测试5: 归还不存在的记录 ==="
curl -s -X POST "$BASE_URL/api/borrows/return/99999" | python3 -m json.tool
echo ""

echo "=== 异常测试6: 归还已归还的记录 ==="
curl -s -X POST "$BASE_URL/api/borrows/borrow" \
  -H "Content-Type: application/json" \
  -d '{"bookId":1,"borrowerName":"testuser2","borrowerPhone":"13900000000","days":30}' | python3 -m json.tool
RECORD_ID=$(curl -s "$BASE_URL/api/borrows?keyword=testuser2" | python3 -c "import sys,json; d=json.load(sys.stdin); print(d['data'][0]['id'] if d['data'] else '')" 2>/dev/null)
curl -s -X POST "$BASE_URL/api/borrows/return/$RECORD_ID" | python3 -m json.tool
curl -s -X POST "$BASE_URL/api/borrows/return/$RECORD_ID" | python3 -m json.tool
echo ""

echo "=== 异常测试7: 添加图书-名称为空 ==="
curl -s -X POST "$BASE_URL/api/books" \
  -H "Content-Type: application/json" \
  -d '{"name":"","author":"test","isbn":"000","category":"test","totalStock":1}' | python3 -m json.tool
echo ""

echo "=== 异常测试8: 添加图书-库存为负数 ==="
curl -s -X POST "$BASE_URL/api/books" \
  -H "Content-Type: application/json" \
  -d '{"name":"test","author":"test","isbn":"000","category":"test","totalStock":-1}' | python3 -m json.tool
echo ""

echo "=== 异常测试9: 更新不存在的图书 ==="
curl -s -X PUT "$BASE_URL/api/books/99999" \
  -H "Content-Type: application/json" \
  -d '{"name":"test","author":"test","isbn":"000","category":"test","totalStock":1}' | python3 -m json.tool
echo ""

echo "=== 异常测试10: 删除不存在的图书 ==="
curl -s -X DELETE "$BASE_URL/api/books/99999" | python3 -m json.tool
echo ""

echo "=== 异常测试11: 无效的JSON格式 ==="
curl -s -X POST "$BASE_URL/api/books" \
  -H "Content-Type: application/json" \
  -d 'invalid json' | python3 -m json.tool 2>/dev/null || echo "返回非JSON格式"
echo ""

echo "=== 异常测试12: 超期记录测试 ==="
curl -s -X POST "$BASE_URL/api/borrows/borrow" \
  -H "Content-Type: application/json" \
  -d '{"bookId":1,"borrowerName":"overdue_user","borrowerPhone":"13700000000","days":-30}' | python3 -m json.tool
echo ""

echo "=========================================="
echo "异常测试完成"
echo "=========================================="
