#!/bin/bash

echo "Atencão: essa sua decisão vai reverter a aplicação para a imagem anterior"
read -p "CONFIRMAR O ROLLBACK? (s/n) : " CONFIRMACAO


if [ "$CONFIRMACAO" != "s" ]; then
	echo "Rollback cancelado."
	exit 0

fi


echo "Parando o Container Atual"
docker compose stop app

echo "Buscando a imagem anterior"
IMAGEM_ANTERIOR=$(docker images containerwatch-app --format "{{.ID}}" | sed -n '2p' )

if [ -z "IMAGEM_ANTERIOR" ]; then
	echo"Nenhuma imagem anterior encontrada."
	exit 1

fi


echo "Restaurando imagem $IMAGEM_ANTERIOR"
docker tag "$IMAGEM_ANTERIOR" containerwatch-app:latest
docker coompose up -d app


echo "Rollback concluido."

