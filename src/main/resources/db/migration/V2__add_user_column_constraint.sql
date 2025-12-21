ALTER TABLE IF  EXISTS spring_file_storage.users
ADD COLUMN IF NOT EXISTS password varchar(255),
ADD COLUMN IF NOT EXISTS role varchar(100),
ADD CONSTRAINT ADD UNIQUE(username);
