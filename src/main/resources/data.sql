delete from  user_role;
delete from  role;
delete from  user;

INSERT INTO role (id, name) VALUES (1, 'ROLE_ADMIN'), (2, 'ROLE_MOD'), (3, 'ROLE_USER');

INSERT INTO user (id, email, password, username) VALUES (1, 'admin@gmail.com', '$2a$10$82OP0ZpfxZWUqjtAW8I8XezFYMI8uSlygTDb5lREBT1tPlE1Ic8VC', 'Admin'), (2, 'user@gmail.com', '$2a$10$82OP0ZpfxZWUqjtAW8I8XezFYMI8uSlygTDb5lREBT1tPlE1Ic8VC', 'User');

insert into user_role(user_id, role_id) values (1,1), (1,2), (1,3), (2,2);
