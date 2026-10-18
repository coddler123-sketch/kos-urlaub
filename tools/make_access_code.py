"""Erzeugt aus dem GitHub-Lese-Token einen kurzen Zugangscode fuer die privaten Fotos.

Aufruf:  python tools/make_access_code.py
Das Token wird verdeckt abgefragt und nie gespeichert. Das Skript schreibt die verschluesselte
Datei bilder-zugang.json (im Repo-Root) und gibt den Code aus, den die App-Nutzer eingeben.
Danach die JSON-Datei committen und pushen, der Code funktioniert ohne neues App-Release.
Bei neuem Token (Ablauf): Skript erneut laufen lassen, neuen Code verteilen.

Verfahren (muss zu PrivateImages.kt passen): PBKDF2-HMAC-SHA1 (Code, Salt) -> 32-Byte-Schluessel,
Pruefsumme und Keystream per SHA-256, Token XOR Keystream.
"""
import getpass
import hashlib
import json
import pathlib
import secrets

ALPHABET = "ABCDEFGHJKMNPQRSTUVWXYZ23456789"  # ohne I, L, O, 0, 1
ITERATIONS = 300_000
CODE_LENGTH = 10  # ca. 50 Bit: offline nicht durchprobierbar


def derive(code: str, salt: bytes, iterations: int) -> bytes:
    return hashlib.pbkdf2_hmac("sha1", code.encode(), salt, iterations, 32)


def encrypt(token: str, code: str, salt: bytes, iterations: int = ITERATIONS) -> dict:
    key = derive(code, salt, iterations)
    data = token.encode()
    stream = b"".join(hashlib.sha256(key + bytes([i])).digest() for i in range(len(data) // 32 + 1))
    return {
        "salt": salt.hex(),
        "iterations": iterations,
        "check": hashlib.sha256(key + b"chk").digest()[:8].hex(),
        "data": bytes(a ^ b for a, b in zip(data, stream)).hex(),
    }


def main() -> None:
    token = getpass.getpass("GitHub-Token (Eingabe bleibt unsichtbar): ").strip()
    if not token:
        raise SystemExit("Kein Token eingegeben.")
    code = "".join(secrets.choice(ALPHABET) for _ in range(CODE_LENGTH))
    out = pathlib.Path(__file__).resolve().parent.parent / "bilder-zugang.json"
    out.write_text(json.dumps(encrypt(token, code, secrets.token_bytes(16))) + "\n")
    print(f"Geschrieben: {out}")
    print(f"Zugangscode: {code[:4]}-{code[4:8]}-{code[8:]}")
    print("Jetzt bilder-zugang.json committen und pushen.")


if __name__ == "__main__":
    main()
