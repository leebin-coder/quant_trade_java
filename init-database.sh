#!/bin/bash

# Database initialization script
# This script creates the quant_trade database if it doesn't exist

DB_HOST=localhost
DB_PORT=5432
DB_USER=libin
DB_NAME=quant_trade

echo "Initializing database: $DB_NAME"
echo "Host: $DB_HOST:$DB_PORT"
echo "User: $DB_USER"
echo ""

# Check if database exists
echo "Checking if database '$DB_NAME' exists..."
DB_EXISTS=$(psql -h $DB_HOST -p $DB_PORT -U $DB_USER -lqt | cut -d \| -f 1 | grep -qw $DB_NAME; echo $?)

if [ $DB_EXISTS -eq 0 ]; then
    echo "Database '$DB_NAME' already exists."
else
    echo "Creating database '$DB_NAME'..."
    psql -h $DB_HOST -p $DB_PORT -U $DB_USER -c "CREATE DATABASE $DB_NAME;"

    if [ $? -eq 0 ]; then
        echo "Database '$DB_NAME' created successfully!"
    else
        echo "Failed to create database '$DB_NAME'."
        exit 1
    fi
fi

echo ""
echo "Database initialization completed!"
echo ""
echo "Next steps:"
echo "1. Start infrastructure services: ./start-infra.sh"
echo "2. Build the project: cd quant-parent && mvn clean install -DskipTests"
echo "3. Run services (Flyway will auto-create tables on first startup)"
echo ""
echo "Tables will be created by Flyway migration scripts in:"
echo "  - quant-user/src/main/resources/db/migration/"
echo "  - quant-trade/src/main/resources/db/migration/"
echo "  - quant-risk/src/main/resources/db/migration/"
echo "  - quant-market/src/main/resources/db/migration/"
echo "  - quant-strategy/src/main/resources/db/migration/"
