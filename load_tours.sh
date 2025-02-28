#!/bin/bash

# Copy the CSV file to the PostgreSQL container
echo "Copying tours.csv to the PostgreSQL container..."
docker cp tours.csv vito-trips-postgres-1:/tmp/tours.csv

# Execute the SQL script
echo "Executing SQL script to load tour data..."
docker exec -i vito-trips-postgres-1 psql -U postgres1 -d vitodb < load_tours.sql

echo "Tour data loaded successfully!" 