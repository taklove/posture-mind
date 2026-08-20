"""
把阿里云 PEM 私钥转成 OpenSSH 格式（Windows OpenSSH 兼容性更好）
"""
from cryptography.hazmat.primitives import serialization
import sys

src = r"C:\Users\Lilydai\.ssh\ali_overseas"
dst = r"C:\Users\Lilydai\.ssh\ali_overseas_openssh"

with open(src, "rb") as f:
    key = serialization.load_pem_private_key(f.read(), password=None)

with open(dst, "wb") as f:
    f.write(key.private_bytes(
        encoding=serialization.Encoding.PEM,
        format=serialization.PrivateFormat.OpenSSH,
        encryption_algorithm=serialization.NoEncryption()
    ))

print("OK converted")
print("Output:", dst)
