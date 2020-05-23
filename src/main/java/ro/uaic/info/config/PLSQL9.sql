create function getType(v_rec_tab DBMS_SQL.DESC_TAB, v_nr_col int) return varchar2 as
  v_tip_coloana varchar2(200);
v_precizie VARCHAR2(40);
begin
    CASE (v_rec_tab(v_nr_col).col_type)
         WHEN 1 THEN v_tip_coloana := 'VARCHAR2'; v_precizie := '(' || v_rec_tab(v_nr_col).col_max_len || ')';
WHEN 2 THEN v_tip_coloana := 'NUMBER'; v_precizie := '(' || v_rec_tab(v_nr_col).col_precision || ',' || v_rec_tab(v_nr_col).col_scale || ')';
WHEN 12 THEN v_tip_coloana := 'DATE'; v_precizie := '';
WHEN 96 THEN v_tip_coloana := 'CHAR'; v_precizie := '(' || v_rec_tab(v_nr_col).col_max_len || ')';
WHEN 112 THEN v_tip_coloana := 'CLOB'; v_precizie := '';
WHEN 113 THEN v_tip_coloana := 'BLOB'; v_precizie := '';
WHEN 109 THEN v_tip_coloana := 'XMLTYPE'; v_precizie := '';
WHEN 101 THEN v_tip_coloana := 'BINARY_DOUBLE'; v_precizie := '';
WHEN 100 THEN v_tip_coloana := 'BINARY_FLOAT'; v_precizie := '';
WHEN 8 THEN v_tip_coloana := 'LONG'; v_precizie := '';
WHEN 180 THEN v_tip_coloana := 'TIMESTAMP'; v_precizie :='(' || v_rec_tab(v_nr_col).col_scale || ')';
WHEN 181 THEN v_tip_coloana := 'TIMESTAMP' || '(' || v_rec_tab(v_nr_col).col_scale || ') ' || 'WITH TIME ZONE'; v_precizie := '';
WHEN 231 THEN v_tip_coloana := 'TIMESTAMP' || '(' || v_rec_tab(v_nr_col).col_scale || ') ' || 'WITH LOCAL TIME ZONE'; v_precizie := '';
WHEN 114 THEN v_tip_coloana := 'BFILE'; v_precizie := '';
WHEN 23 THEN v_tip_coloana := 'RAW'; v_precizie := '(' || v_rec_tab(v_nr_col).col_max_len || ')';
WHEN 11 THEN v_tip_coloana := 'ROWID'; v_precizie := '';
WHEN 109 THEN v_tip_coloana := 'URITYPE'; v_precizie := '';
END CASE;
return v_tip_coloana||v_precizie;
end;
/



create or replace procedure generateCatalogueForCourseID(courseID in int) as
              courseName varchar2(64);
noteCursorID number;
studentiCursorID number;
cursorID number;
ok number;

totalColoane number;
numarColoana number;
recordTab DBMS_SQL.desc_tab;

counter number;

ex_tableExists exception;
pragma exception_init ( ex_tableExists, -20005 );

createCatalogueStatementString varchar2(300);
begin
select titlu_curs into courseName from CURSURI where id = courseID;

courseName := replace(upper(courseName), ' ', '_');

/*
createCatalogueStatementString := 'CREATE TABLE ' || courseName || '( id int not null primary key, ';
 */

select count(*) into counter from USER_TABLES where TABLE_NAME = courseName;

DBMS_OUTPUT.PUT_LINE(courseName || ' ' || counter);

if(counter > 0) then
        raise  ex_tableExists;
end if;

createCatalogueStatementString := 'CREATE TABLE ' || courseName || '(';

studentiCursorID := DBMS_SQL.open_cursor;
dbms_sql.parse(studentiCursorID, 'select nr_matricol, nume, prenume from studenti', dbms_sql.native);
ok := dbms_sql.EXECUTE(studentiCursorID);
dbms_sql.DESCRIBE_COLUMNS(studentiCursorID, totalColoane, recordTab);

numarColoana := recordTab.FIRST;
if(numarColoana is not null) then
        loop
            createCatalogueStatementString := createCatalogueStatementString || recordTab(numarColoana).col_name || ' ' || getType(recordTab, numarColoana) || ',';
numarColoana := recordTab.next(numarColoana);
exit when (numarColoana is null);
end loop;
end if;
dbms_sql.CLOSE_CURSOR(studentiCursorID);

noteCursorID := dbms_sql.open_cursor;
dbms_sql.parse(noteCursorID, 'select valoare, data_notare from note', dbms_sql.native);
ok := dbms_sql.EXECUTE(noteCursorID);
dbms_sql.DESCRIBE_COLUMNS(noteCursorID, totalColoane, recordTab);

numarColoana := recordTab.FIRST;
if(numarColoana is not null) then
        loop
            createCatalogueStatementString := createCatalogueStatementString || recordTab(numarColoana).col_name || ' ' || getType(recordTab, numarColoana) || ',';
numarColoana := recordTab.next(numarColoana);
exit when (numarColoana is null);
end loop;
end if;
dbms_sql.CLOSE_CURSOR(noteCursorID);

createCatalogueStatementString := substr(createCatalogueStatementString, 0, length(createCatalogueStatementString) - 1) || ')';

DBMS_OUTPUT.PUT_LINE(createCatalogueStatementString);

cursorID := dbms_sql.OPEN_CURSOR;

dbms_sql.parse(cursorID, createCatalogueStatementString, DBMS_SQL.native);
ok := dbms_sql.EXECUTE(cursorID);
dbms_sql.CLOSE_CURSOR(cursorID);

cursorID := dbms_sql.OPEN_CURSOR;

dbms_sql.parse(cursorID, 'insert into ' || courseName ||  ' select NR_MATRICOL, nume, prenume, valoare, DATA_NOTARE from cursuri join note on cursuri.id = note.id_curs join studenti on studenti.id = note.id_student where ID_CURS = ' || courseID, DBMS_SQL.native);
ok := dbms_sql.EXECUTE(cursorID);
dbms_sql.CLOSE_CURSOR(cursorID);

cursorID := dbms_sql.OPEN_CURSOR;

dbms_sql.parse(cursorID, 'commit', DBMS_SQL.native);
ok := dbms_sql.EXECUTE(cursorID);
dbms_sql.CLOSE_CURSOR(cursorID);

    /*
    cursorID := dbms_sql.OPEN_CURSOR;

    dbms_sql.parse(cursorID, 'create sequence id_' || courseName || '_auto_inc_seq start with 1', DBMS_SQL.native);
    ok := dbms_sql.EXECUTE(cursorID);
    dbms_sql.CLOSE_CURSOR(cursorID);

    cursorID := dbms_sql.OPEN_CURSOR;

    dbms_sql.parse(cursorID, 'create or replace trigger id_' || courseName || '_inc_trigger before' ||
                             ' insert on ' || courseName || ' for each row ' ||
                             'begin select id_' || courseName || '_auto_inc_seq.nextval into :new.id from dual; end;', DBMS_SQL.native);
    ok := dbms_sql.EXECUTE(cursorID);
    dbms_sql.CLOSE_CURSOR(cursorID);

    cursorID := dbms_sql.OPEN_CURSOR;

    dbms_sql.parse(cursorID, 'insert into ' || courseName ||  ' select NR_MATRICOL, nume, prenume, valoare, DATA_NOTARE from cursuri join note on cursuri.id = note.id_curs join studenti on studenti.id = note.id where ID_CURS = ' || courseID, DBMS_SQL.native);
    ok := dbms_sql.EXECUTE(cursorID);
    dbms_sql.CLOSE_CURSOR(cursorID);

    cursorID := dbms_sql.OPEN_CURSOR;

    dbms_sql.parse(cursorID, 'drop sequence id_' || courseName || '_auto_inc_seq', DBMS_SQL.native);
    ok := dbms_sql.EXECUTE(cursorID);
    dbms_sql.CLOSE_CURSOR(cursorID);

    cursorID := dbms_sql.OPEN_CURSOR;

    dbms_sql.parse(cursorID, 'drop trigger id_' || courseName || '_inc_trigger', DBMS_SQL.native);
    ok := dbms_sql.EXECUTE(cursorID);
    dbms_sql.CLOSE_CURSOR(cursorID);
    */

end;
/
