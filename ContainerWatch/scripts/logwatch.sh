#!/bin/bash

SERVICOS=("cw-servico-1" "cw-servico-2" "cw-servico-3" "containerwatch-app-1")

echo "Monitorando logs de : ${SERVICOS[@]}"

echo "Pressione Ctrl +C para sair"



for CONTAINER in "${SERVICOS~[@]}"; do

docker logs -f "$CONTAINER" 2>%1 | sed"s/^/[$CONTAINER] /" & 

done


grep --color=always -IE "error|exception|fail|critical\$" &


wait
