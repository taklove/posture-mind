#!/bin/bash
# 检查阿里云 DNS 上的 A 记录
echo "=== 阿里云 DNS (dns32.hichina.com) ==="
for d in manus.xin www.manus.xin; do
  echo "--- $d ---"
  dig +short $d @dns32.hichina.com 2>/dev/null
done
echo ""
echo "=== 阿里云 DNS (dns31.hichina.com) ==="
for d in manus.xin www.manus.xin; do
  echo "--- $d ---"
  dig +short $d @dns31.hichina.com 2>/dev/null
done
echo ""
echo "=== 本地 DNS 缓存 ==="
for d in manus.xin www.manus.xin; do
  echo "--- $d ---"
  getent ahosts $d 2>/dev/null
done
