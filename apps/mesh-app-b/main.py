import os

port = int(os.environ.get("PORT", default=8080))


async def app(scope, receive, send):
    assert scope["type"] == "http"

    match scope["path"]:
        case "/":
            text, status = await index()
        case "/health":
            text, status = await health()
        case _:
            text, status = await page_not_found()

    response_headers = [(b"content-type", b"text/plain; charset=utf-8")]
    await send({"type": "http.response.start", "status": status, "headers": response_headers})
    await send({"type": "http.response.body", "body": text.encode("UTF-8")})


async def index():
    return f"Service B", 200


async def health():
    return "UP", 200


async def page_not_found():
    return "Page not found", 404


if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=port, proxy_headers=True, server_header=False, access_log=False)