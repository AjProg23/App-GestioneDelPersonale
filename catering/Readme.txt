Come usare sqlite3:

Aprire cmd, navigare fino alla cartella che contine il db stesso (nel nostro caso cartella "database")
Dare commando sqlite3 catering.db e ora possiamo usare query del tièpo SELECT * FROM Vacation WHERE staff_member_id = 2;

Per uscire basta un Ctrl + C

Per aggiornare il Db:  Get-Content database\catering_init_sqlite.sql | sqlite3 database\catering.db