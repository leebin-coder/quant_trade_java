#!/bin/bash

echo "Starting infrastructure services..."
echo "Note: Using external PostgreSQL at localhost:5432 (user: libin)"
echo ""

docker-compose -f docker-compose-infra.yml up -d

echo ""
echo "Waiting for services to be ready..."
sleep 15

echo ""
echo "Infrastructure services started:"
echo "  - PostgreSQL: localhost:5432 (External - using your existing database)"
echo "  - RabbitMQ: localhost:5672 (Management: http://localhost:15672, guest/guest)"
echo "  - Nacos: http://localhost:8848/nacos (nacos/nacos)"

echo ""
echo "Docker container status:"
docker-compose -f docker-compose-infra.yml ps

echo ""
echo "To verify PostgreSQL connection:"
echo "  psql -h localhost -p 5432 -U libin -d quant_trade"
