#!/bin/bash


TIMESTAMP=$(date +%F)
ARQUIVO="backup_containerwatch_${TIMESTAMP}.sql"

echo "Gerando backup do banco em ${ARQUIVO}"

docker compose exec -T db pg_dump -U containerwatch containerwatch > "$ARQUIVO"

if [ $? -eq 0 ]; then
	echo "Backup salvo com sucesso: $ARQUIVO"

else 
	echo "Falha ao gerar backup."
	exit 1

fi
