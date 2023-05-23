-- scf.payment_batch definition

-- Drop table

-- DROP TABLE scf.payment_batch;
CREATE SCHEMA custom;

CREATE TABLE custom.users
(
    uuid uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    name varchar,
    age  int
);
