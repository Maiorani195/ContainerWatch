#!/bin/bash


APP_URL="http://localhost:8080"
COMANDO_HEALTH_CHECK="$APP_URL/actuator/health"

echo "Subindo o ambiente ContainerWatch"
docker compose up -d --build


echo "Aguardando Aplicação"

until curl -s -f "$COMANDO_HEALTH_CHECK" > /dev/null 2>&1; do
	echo "Ainda não está pronto, aguarde.."

	sleep 2

done

echo "Aplicação pronta e disponível em $APP_URL"



