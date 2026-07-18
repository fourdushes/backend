#!/usr/bin/env bash

set -euo pipefail

DEPLOY_DIR="${DEPLOY_DIR:-/opt/hearo}"
cd "${DEPLOY_DIR}"

if [[ ! -f .env ]]; then
  echo "Missing ${DEPLOY_DIR}/.env" >&2
  exit 1
fi

ECR_REGISTRY="$(sed -n 's/^ECR_REGISTRY=//p' .env | tail -n 1)"
AWS_REGION="$(sed -n 's/^AWS_REGION=//p' .env | tail -n 1)"

if [[ -z "${ECR_REGISTRY}" || -z "${AWS_REGION}" ]]; then
  echo "ECR_REGISTRY and AWS_REGION must be set in ${DEPLOY_DIR}/.env" >&2
  exit 1
fi

aws ecr get-login-password \
  --region "${AWS_REGION}" \
  | docker login \
      --username AWS \
      --password-stdin "${ECR_REGISTRY}"

docker compose pull
docker compose up -d --remove-orphans
docker image prune -f
docker compose ps
