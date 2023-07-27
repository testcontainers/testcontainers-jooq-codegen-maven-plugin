CREATE TABLE custom.users
(
    uuid uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    name varchar,
    age  int
);
