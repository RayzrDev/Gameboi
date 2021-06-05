#!/bin/bash

cd "$(dirname "$(realpath "$0")")/.." || exit 1

docker build -t "${1:-rayzr522}/gameboi:latest" -f .deploy/Dockerfile .
