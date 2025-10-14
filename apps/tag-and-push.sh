#!/bin/sh

if [[ $QUAY_USER ]]
then
    echo "## Tagging and pushing distributed-tracing-app-a"
    podman tag distributed-tracing-app-a:latest quay.io/$QUAY_USER/distributed-tracing-app-a:latest
    podman push quay.io/$QUAY_USER/distributed-tracing-app-a:latest
    echo "## distributed-tracing-app-a successfully pushed to quay.io"

    echo "## Tagging and pushing distributed-tracing-app-b"
    podman tag distributed-tracing-app-b:latest quay.io/$QUAY_USER/distributed-tracing-app-b:latest
    podman push quay.io/$QUAY_USER/distributed-tracing-app-b:latest
    echo "## distributed-tracing-app-b successfully pushed to quay.io"

    echo "## Tagging and pushing distributed-tracing-app-c"
    podman tag distributed-tracing-app-c:latest quay.io/$QUAY_USER/distributed-tracing-app-c:latest
    podman push quay.io/$QUAY_USER/distributed-tracing-app-c:latest
    echo "## distributed-tracing-app-c successfully pushed to quay.io"

    echo "## Tagging and pushing distributed-tracing-mesh-app-a"
    podman tag distributed-tracing-mesh-app-a:latest quay.io/$QUAY_USER/distributed-tracing-mesh-app-a:latest
    podman push quay.io/$QUAY_USER/distributed-tracing-mesh-app-a:latest
    echo "## distributed-tracing-mesh-app-a successfully pushed to quay.io"

    echo "## Tagging and pushing distributed-tracing-mesh-app-b"
    podman tag distributed-tracing-mesh-app-b:latest quay.io/$QUAY_USER/distributed-tracing-mesh-app-b:latest
    podman push quay.io/$QUAY_USER/distributed-tracing-mesh-app-b:latest
    echo "## distributed-tracing-mesh-app-b successfully pushed to quay.io"

    echo "DONE"
else
    echo "Set your quay.io username as QUAY_USER environment variable"
fi
