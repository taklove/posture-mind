"""
把 nginx.conf 里的默认 server 块删掉，让我们的 posturemind.conf 接管 80 端口
"""
import re

src = "/etc/nginx/nginx.conf"
with open(src, "r") as f:
    content = f.read()

# 匹配 http {} 块里的 server { ... } 块
# 用更宽松的括号匹配
out = []
i = 0
while i < len(content):
    # 找 "server {" 块起点（不在字符串内）
    idx = content.find("\n    server {", i)
    if idx == -1:
        out.append(content[i:])
        break
    out.append(content[i:idx])
    # 找匹配的 }
    depth = 0
    j = idx
    found = False
    while j < len(content):
        c = content[j]
        if c == '{':
            depth += 1
        elif c == '}':
            depth -= 1
            if depth == 0:
                # 找到匹配，j 是 }
                j += 1
                # 跳过后续空行
                while j < len(content) and content[j] in '\n\r':
                    j += 1
                found = True
                break
        j += 1
    if not found:
        out.append(content[idx:])
        break
    i = j

with open(src, "w") as f:
    f.write("".join(out))

print("Removed default server blocks")
