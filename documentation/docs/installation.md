# Setup and Installation

## Requirements
- One or multiple PaperMC (1.19+) servers.
- MariaDB (or MySQL) database.

## Installation

1. Download the plugin and put it into your `plugins` directory.
2. (Re)start the server.
3. Open `config.yml` and enter your database credentials. If you don't know how to do this, read the next chapter.
4. Restart the server again or reload the config using `/levels debug reload config`
5. If you see no error in the console, the database connection should be established successfully. You can use `/levels debug database info` to check it if you want to make sure.
6. Start using the plugin

## Simple database setup
This guide is for Linux. If you use a Windows server and, you are on your own (and btw, you shouldn't use Windows for servers unless you are a masochist).

1. Install MariaDB: `sudo apt install mariadb-server mariadb-client` or `sudo dnf install mariadb-server`.
2. Let it start on OS startup: `sudo systemctl enable --now mariadb`
3. Start MariaDB setup: `sudo mariadb_secure_installation`.
4. Answer the questions you're getting asked. Don't create security risks here. Disallow remote root login, select a password, remove test database, etc...
5. Open MariaDB console: `sudo mariadb -u root`. As the root user on the OS, you can log into MariaDB without a root password.
6. Create a database for PlayerLevels: `CREATE DATABASE playerlevels;`
7. Create a user for PlayerLevels: `CREATE USER 'db_playerlevels'@'localhost' IDENTIFIED BY 'your secure password'`. **Do not use the root user for PlayerLevels!**
8. Grant privileges: `GRANT ALL PRIVILEGES ON db_playerlevels TO 'db_playerlevels'@'localhost';` and `FLUSH PRIVILEGES;`.
9. Enter the credentials. In this example, `db_playerlevels` is your user, `your secure password` your password, `3306` your port and `playerlevels` your database.

If you are using MySQL, it should be mostly the same, but I would recommend you to use MariaDB since my plugin is developed against that.

## Next steps
- Choose xp formula: You might not like the default xp formula, so change it.
- Add some rewards and remove the default one.
- Set server ids for your servers.
- Change the plugin messages or disable some by replacing the message string with `""`.
- Read the documentation to learn more about the plugin.