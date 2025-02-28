-- First, create a temporary table to hold the CSV data
CREATE TEMPORARY TABLE TEMP_TOURS (
    TOUR_NAME VARCHAR(100),
    LOCATION VARCHAR(255),
    DESCRIPTION VARCHAR(255)
);

-- Copy data from CSV file
COPY temp_tours FROM '/tmp/tours.csv' WITH (FORMAT csv, HEADER true, DELIMITER ',', QUOTE '"');

-- Insert data into the tours table with timestamps
INSERT INTO TOURS (
  TOUR_NAME,
  LOCATION,
  DESCRIPTION,
  CREATED_AT,
  UPDATED_AT
)
  SELECT
    TOUR_NAME,
    LOCATION,
    DESCRIPTION,
    NOW(), -- created_at
    NOW() -- updated_at
  FROM
    TEMP_TOURS;

-- Drop the temporary table
DROP TABLE TEMP_TOURS;

-- Show the inserted data
SELECT
  *
FROM
  TOURS;