#!/bin/bash

API_URL="http://localhost:8080/eventos"
SERVICOS=("cw-servico-1" "cw-servico-2" "cw-servico-3")
INTERVALO=10

while true; do
    for CONTAINER in "${SERVICOS[@]}"; do
        STATUS=$(docker inspect -f '{{.State.Running}}' "$CONTAINER" 2>/dev/null)

        if [ "$STATUS" == "true" ]; then
            echo "[$CONTAINER] OK"
            curl -s -X POST "$API_URL" \
                -H "Content-Type: application/json" \
                -d "{\"containerName\": \"$CONTAINER\", \"status\": \"UP\", \"mensagem\": \"health check ok\", \"tempoDeRespostaMs\": null}" \
                > /dev/null
        else
            echo "[$CONTAINER] CAIU - tentando reiniciar"
            docker start "$CONTAINER" > /dev/null 2>&1

            sleep 2
            STATUS_APOS_RESTART=$(docker inspect -f '{{.State.Running}}' "$CONTAINER" 2>/dev/null)

            if [ "$STATUS_APOS_RESTART" == "true" ]; then
                echo "[$CONTAINER] REINICIADO com sucesso"
                curl -s -X POST "$API_URL" \
                    -H "Content-Type: application/json" \
                    -d "{\"containerName\": \"$CONTAINER\", \"status\": \"REINICIANDO\", \"mensagem\": \"container reiniciado automaticamente\", \"tempoDeRespostaMs\": null}" \
                    > /dev/null
            else
                echo "[$CONTAINER] FALHA ao reiniciar"
                curl -s -X POST "$API_URL" \
                    -H "Content-Type: application/json" \
                    -d "{\"containerName\": \"$CONTAINER\", \"status\": \"FALHA_REINICIO\", \"mensagem\": \"nao foi possivel reiniciar o container\", \"tempoDeRespostaMs\": null}" \
                    > /dev/null
            fi
        fi
    done

    sleep "$INTERVALO"
done
