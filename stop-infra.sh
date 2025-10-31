#!/bin/bash

echo "Stopping infrastructure services..."
docker-compose -f docker-compose-infra.yml down

echo "Infrastructure services stopped."
