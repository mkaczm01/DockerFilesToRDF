%===============================================================================
% Proedura startowa
% s() :- start_local("apt-cacher-ng").
% s() :- start_local("aspnet-mssql-compose").
% s() :- start_local("django").
% s() :- start_local("dotnetcore").
s() :- start_local("dockeronto").

/*
swipl -s file.pl -g "mygoal(3,foo)." -t halt.

swipl.exe -s E:\DockerFilesToRDF\DockerFilesToRDF\dockerfiles.pl -g "main()." -q -t halt -- E:\DockerFilesToRDF\DockerFilesToRDF\sources\dockeronto -- E:\DockerFilesToRDF\DockerFilesToRDF\rdf\dockeronto
*/

main :-
  current_prolog_flag(argv, ARGS),
	nth1(1, ARGS, INPUT),
	nth1(2, ARGS, OUTPUT),
	start(INPUT, OUTPUT).

start(FILE_INPUT, FILE_OUTPUT) :-
	read_file_to_codes(FILE_INPUT, FILE_CONTENT, []),
	headers(HEADERS),
	body(BODY, FILE_CONTENT, []),
	concat_atom([HEADERS, BODY], OUTPUT),
	saveFile(FILE_OUTPUT, OUTPUT),
	write(OUTPUT), !.

start_local(FILE) :-
	concat_atom(["./sources/", FILE], INPUT_FILE),
	read_file_to_codes(INPUT_FILE, FILE_CONTENT, []),
	headers(HEADERS),
	body(BODY, FILE_CONTENT, []),
	concat_atom([HEADERS, BODY], OUTPUT),
	concat_atom(["./rdf/", FILE], OUTPUT_FILE),
	saveFile(OUTPUT_FILE, OUTPUT),
	write(OUTPUT), !.

%===============================================================================
% Zapisywanie treści do pliku
saveFile(FILE_NAME, CONTENT):-
  open(FILE_NAME, write, STREAM),
  write(STREAM, CONTENT),
  close(STREAM).

%===============================================================================
% do debugowania programu
swi_debug(TEXT) :- write('DEBUG:'), writeln(TEXT).

%===============================================================================
%

headers(HEADER) :- concat_atom(['@prefix do: <http://linkedcontainers.org/vocab#> .
@prefix fno: <https://w3id.org/function/ontology#> .
@prefix ex: <http://www.example.org/#> .
@base <http://linkedcontainers.org/vocab> .

ex:dockerfileX a do:Dockerfile;
do:contains ( ex:insX ex:insX ex:insX ex:insX );\n
'], HEADER).

body(BODY) --> new_line_or_space, custom_code(CODE), {concat_atom([CODE], BODY)}.

% custom_code(CODE) --> space, docker_instructions(CODE_1), new_line, {custom_code(CODE_2), { concat_atom([CODE_1, "\n", CODE_2], CODE) }.
custom_code(CODE) --> space, docker_instructions(CODE, 1).
custom_code('') --> "".

%===============================================================================
%
docker_instructions(INSTRUCTIONS, ID) --> space, docker_instruction(I, ID), new_line, {NEW_ID is ID+1}, docker_instructions(REST, NEW_ID), {concat_atom([I, '\n', REST], INSTRUCTIONS)}.
docker_instructions(INSTRUCTION, ID) --> space, docker_instruction(INSTRUCTION, ID), new_line.

docker_instruction(INSTRUCTION, _) --> comment(INSTRUCTION).
docker_instruction(INSTRUCTION, ID) --> from(INSTRUCTION, ID).
docker_instruction(INSTRUCTION, ID) --> run(INSTRUCTION, ID).
docker_instruction(INSTRUCTION, ID) --> cmd(INSTRUCTION, ID).
docker_instruction(INSTRUCTION, ID) --> label(INSTRUCTION, ID).
docker_instruction(INSTRUCTION, ID) --> expose(INSTRUCTION, ID).
docker_instruction(INSTRUCTION, ID) --> env(INSTRUCTION, ID).
docker_instruction(INSTRUCTION, ID) --> add(INSTRUCTION, ID).
docker_instruction(INSTRUCTION, ID) --> copy(INSTRUCTION, ID).
docker_instruction(INSTRUCTION, ID) --> volume(INSTRUCTION, ID).
docker_instruction(INSTRUCTION, ID) --> user(INSTRUCTION, ID).
docker_instruction(INSTRUCTION, ID) --> workdir(INSTRUCTION, ID).
docker_instruction(INSTRUCTION, ID) --> stopsignal(INSTRUCTION, ID).

%===============================================================================
% # comment - OK
comment(LINE) --> "#", space, string(COMMENT), "\n", {concat_atom(['# ', COMMENT], LINE)}.

%===============================================================================
% FROM
% FROM <image> [AS <name>] - OK
% FROM <image>[:<tag>] [AS <name>] - OK
% FROM <image>[@<digest>] [AS <name>] - NOK
% FROM microsoft/aspnetcore-build:lts
from(LINE, ID) --> "FROM", space, string(IMAGE), if_from_tag(TAG), if_from_as(AS), new_line, {concat_atom(["ex:ins", ID, " fno:executes . do:from do:fromValue <", IMAGE, TAG, AS, ">;"], LINE)}.

if_from_as(STRING) --> " ", "AS", " ", string(NAME), {concat_atom(['AS ', NAME], STRING)}.
if_from_as("") --> "".

if_from_tag(STRING) --> ":", if_from_tag_name(TAG), {concat_atom([':', TAG], STRING)}.
if_from_tag("") --> "".

if_from_tag_name(NAME) --> variable_name(NAME).
if_from_tag_name(NAME) --> int(NAME).


%===============================================================================
% RUN
% RUN <command> - OK
% RUN ["executable", "param1", "param2"] - NOK
run(LINE, ID) --> "RUN", space, string(COMMAND), new_line, {concat_atom(["ex:ins", ID, " fno:executes . do:run do:Instruction \"", COMMAND, "\" ."], LINE)}.

%===============================================================================
% CMD
cmd(LINE, ID) --> "CMD", space, string(COMMAND), new_line, {concat_atom(["ex:ins", ID," fno:executes . do:cmd do:Instruction \"", COMMAND, "\" ."], LINE)}.

% TODO: opracować
% CMD ["/usr/lib/postgresql/9.3/bin/postgres", "-D", "/var/lib/postgresql/9.3/main", "-c", "config_file=/etc/postgresql/9.3/main/postgresql.conf"]

%===============================================================================
% LABEL
% LABEL <key>=<value> <key>=<value> <key>=<value> ...
label(LINE, ID) --> "LABEL", space, key_value_pairs(PAIR), new_line, {concat_atom(["ex:ins", ID, " fno:executes . do:label do:Instruction \"", PAIR, "\" ."], LINE)}.

key_value_pairs(PAIRS) --> key_value_pair(PAIR), space, key_value_pairs(REST), {concat_atom([PAIR,' ', REST], PAIRS)}.
key_value_pairs(PAIRS) --> key_value_pair(PAIRS).
key_value_pairs('') --> "".

key_value_pair(PAIR) --> if_qoute_mark(_), string(KEY), if_qoute_mark(_), "=", if_qoute_mark(_), string(VALUE), if_qoute_mark(_), { concat_atom([KEY, '===', VALUE], PAIR) }.
key_value_pair(PAIR) --> string(KEY), "=", string(VALUE), { concat_atom([KEY, '===', VALUE], PAIR) }.

if_qoute_mark(STRING) --> "\"", {concat_atom(['"'], STRING)}.
if_qoute_mark("") --> "".
%===============================================================================
% EXPOSE
% EXPOSE 80 - OK
% EXPOSE 80/tcp - OK
expose(LINE, ID) --> "EXPOSE", space, int(PORT), if_expose_protocol(PROTOCOL), new_line, {concat_atom(["ex:ins", ID, " fno:executes . do:expose do:Instruction \"", PORT, PROTOCOL, "\" ."], LINE)}.

if_expose_protocol(STRING) --> "/", variable_name(PROTOCOL), {concat_atom(['/', PROTOCOL], STRING)}.
if_expose_protocol("") --> "".

%===============================================================================
% ENV
% ENV PYTHONUNBUFFERED 1 - NOK
% ENV PYTHONUNBUFFERED=1 - OK
env(LINE, ID) --> "ENV", space, variable_name(KEY), assign(A), string(VALUE), new_line, {concat_atom(["ex:ins", ID, " fno:executes . do:env do:Instruction \"", KEY, "=", VALUE, "\" ."], LINE)}.
assign(A) --> space, "=", space, {concat_atom([''], A)}.
assign(A) --> " ", {concat_atom([''], A)}.
% TODO: opracowac przypisanie ze spacja

%===============================================================================
% ADD
% TODO: opracować
add(LINE, ID) --> "ADD", space, string(VALUE), new_line, {concat_atom(["ex:ins", ID, " fno:executes . do:add do:Instruction \"", VALUE, "\" ."], LINE)}.

%===============================================================================
% COPY
% COPY <src> <dest> - OK
copy(LINE, ID) --> "COPY", space, string(SRC), " ", string(DEST), {concat_atom(["ex:ins", ID, " fno:executes . do:copy do:Instruction \"", SRC, " ", DEST, "\" ."], LINE) }.


%===============================================================================
% ENTRYPOINT
% ENTRYPOINT command param1 - OK
entrypoint(LINE, ID) --> "ENTRYPOINT", space, string(COMMAND), " ", string(PARAM), {concat_atom(["ex:ins", ID, " fno:executes . do:entrypoint do:Instruction \"", COMMAND, " ", PARAM, "\" ."], LINE) }.


%===============================================================================
% VOLUME
% VOLUME ["/myvol"]
% VOLUME /myvol
volume(LINE, ID) --> "VOLUME", space, "[\"", string(DIRECTORY), "\"]", new_line, {concat_atom(["ex:ins", ID," fno:executes . do:volume do:Instruction \"", DIRECTORY, "\" ."], LINE)}.
volume(LINE, ID) --> "VOLUME", space, string(DIRECTORY), new_line, {concat_atom(["ex:ins", ID," fno:executes . do:volume do:Instruction \"", DIRECTORY, "\" ."], LINE)}.

%===============================================================================
% USER
% USER <user>[:<group>]
% USER <UID>[:<GID>]
% USER <user> - OK
user(LINE, ID) --> "USER", space, variable_name(USER), new_line, {concat_atom(["ex:ins", ID, " fno:executes . do:user do:Instruction \"", USER, "\" ."], LINE)}.
% TODO: dodanie grupy?

%===============================================================================
% WORKDIR
% WORKDIR /path/to/workdir - OK
workdir(LINE, ID) --> "WORKDIR", space, string(PATH), new_line, {concat_atom(["ex:ins", ID, " fno:executes . do:workdir do:Instruction \"", PATH, "\" ."], LINE)}.


%===============================================================================
% ARG
% TODO: to odpuszczam

%===============================================================================
% ONBUILD
% TODO: to odpuszczam

%===============================================================================
% STOPSIGNAL
% STOPSIGNAL signal - OK
stopsignal(LINE, ID) --> "STOPSIGNAL", space, string(SIGNAL), new_line, {concat_atom(["STOPSIGNAL: ", SIGNAL], LINE)}.

%===============================================================================
% HEALTHCHECK
% TODO: to odpuszczam

%===============================================================================
% SHELL
% TODO: to odpuszczam







%===============================================================================
% Ścieżka do folderu
% /foo/bar/baz
% path(STRING) --> "/",

%===============================================================================
% Nazwa zmiennej
% foo bar baz
variable_name(CHARS) --> variable_characters(CHARS).

variable_characters(CHARS) --> variable_available_char(CHAR), variable_characters(REST), {concat_atom([CHAR, REST], CHARS)}.
variable_characters(CHARS) --> variable_available_char(CHAR), {concat_atom([CHAR], CHARS)}.

% Char is a letter (upper- or lowercase) or the underscore (_). These are valid first characters for C and Prolog symbols.
variable_available_char(LETTER) --> [CHAR], {code_type(CHAR, csymf), atom_codes(LETTER, [CHAR])}.

%===============================================================================
% SPACJA lub TAB lub NIC
space --> " ", space.
space --> "\t", space.
space --> "".

%===============================================================================
% SPACJA lub TAB
space_req --> " ", space_req.
space_req --> "\t", space_req.
space_req --> " ".
space_req --> "\t".

%===============================================================================
% ENTER lub NIC
new_line --> "\n", new_line.
new_line --> "".

%===============================================================================
% SPACJA lub ENTER lub NIC
new_line_or_space --> space, new_line.
new_line_or_space --> new_line.
new_line_or_space --> space.
new_line_or_space --> "".

%===============================================================================
% Nazwa identyfikatora, np. klasy, zmiennej, funkcji
identifier_name(STRING) --> identifier_first_letter(FIRST_LETTER), name_available_chars(REST), {concat_atom([FIRST_LETTER, REST], STRING)}.
identifier_name(STRING) --> identifier_first_letter(FIRST_LETTER), {concat_atom([FIRST_LETTER], STRING)}.


% csymf - char is a letter (upper- or lowercase) or the underscore (_). These are valid first characters for C and Prolog symbols.
identifier_first_letter(LETTER) --> [CHAR], {code_type(CHAR, csymf), atom_codes(LETTER, [CHAR])}.

name_available_chars(CHARS) --> name_available_characters(CHARS).

name_available_characters(CHARS) --> name_available_char(CHAR), name_available_characters(REST), {concat_atom([CHAR, REST], CHARS)}.
name_available_characters(CHARS) --> name_available_char(CHAR), {concat_atom([CHAR], CHARS)}.

% csym => Char is a letter (upper- or lowercase), digit or the underscore (_). These are valid C and Prolog symbol characters.
name_available_char(LETTER) --> [CHAR], {code_type(CHAR, csym), atom_codes(LETTER, [CHAR])}.


%===============================================================================
% Wartosc numeryczna
int(VALUE) --> int_value(VAL), int(REST), {concat_atom([VAL, REST], VALUE)}.
int(VALUE) --> int_value(VAL), {concat_atom([VAL], VALUE)}.
% digit - Char is a digit.
int_value(VALUE) --> [CHAR], {code_type(CHAR, digit), atom_codes(VALUE, [CHAR])}.

%===============================================================================
%ciągi znaków
string(C) --> chars(C).

chars(C) --> char(C1), chars(Rest), {concat_atom([C1, Rest], C)}.
chars(C) --> char(C).

char('\\"') --> "\\\"".
char('')--> "\"", {!, fail}.

char(C) --> [C1], {code_type(C1, alnum), atom_codes(C, [C1])}.
char(C) --> [C1], {code_type(C1, punct), atom_codes(C, [C1])}.
char(C) --> [C1], {code_type(C1, space), not(code_type(C1, newline)), atom_codes(C, [C1])}.
char('-') --> "-".
char('.') --> ".".