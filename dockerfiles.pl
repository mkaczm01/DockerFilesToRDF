%===============================================================================
% Proedura startowa
s() :- start().

start() :-
	read_file_to_codes("./sources/test", FILE_CONTENT, []),

	% headers(HEADERS),
	body(BODY, FILE_CONTENT, []),
	% footer(FOOTER),

	concat_atom([BODY], OUTPUT),
	saveFile("./rdf/test", OUTPUT),
	write(OUTPUT), !.

%===============================================================================
% Zapisywanie treści do pliku
saveFile(FILE_NAME, CONTENT):-
  open(FILE_NAME, write, STREAM),
  write(STREAM, CONTENT),
  close(STREAM).

%===============================================================================
% do debugowania programu
% ON
swi_debug(TEXT) :- write('DEBUG:'), writeln(TEXT).

%===============================================================================
%
body(BODY) --> new_line_or_space, custom_code(CODE), {concat_atom([CODE], BODY)}.

% custom_code(CODE) --> comment(CODE_1), new_line, custom_code(CODE_2), { concat_atom([CODE_1, "\n", CODE_2], CODE) }.

custom_code(CODE) --> space, docker_instructions(CODE_1), new_line, custom_code(CODE_2), { concat_atom([CODE_1, "\n", CODE_2], CODE) }.

custom_code(CODE) --> space, docker_instructions(CODE).

custom_code('', _) --> "".


%===============================================================================
%
docker_instructions(INSTRUCTIONS) --> space, docker_instruction(I), new_line, docker_instructions(REST), {concat_atom([I, '\n', REST], INSTRUCTIONS)}.
docker_instructions(INSTRUCTION) --> space, docker_instruction(INSTRUCTION), new_line.

docker_instruction(INSTRUCTION) --> comment(INSTRUCTION).
docker_instruction(INSTRUCTION) --> from(INSTRUCTION).
docker_instruction(INSTRUCTION) --> run(INSTRUCTION).
docker_instruction(INSTRUCTION) --> cmd(INSTRUCTION).
docker_instruction(INSTRUCTION) --> label(INSTRUCTION).
docker_instruction(INSTRUCTION) --> expose(INSTRUCTION).
docker_instruction(INSTRUCTION) --> env(INSTRUCTION).

docker_instruction(INSTRUCTION) --> volume(INSTRUCTION).
docker_instruction(INSTRUCTION) --> user(INSTRUCTION).
docker_instruction(INSTRUCTION) --> workdir(INSTRUCTION).
docker_instruction(INSTRUCTION) --> stopsignal(INSTRUCTION).

%===============================================================================
% komentarz
comment(LINE) --> "#", space, string(COMMENT), new_line, {concat_atom(["kom: ", COMMENT], LINE)}.

%===============================================================================
% FROM
from(LINE) --> "FROM", space, string(IMAGE), if_from_as(AS), new_line, {concat_atom(["FROM: ", IMAGE, AS], LINE)}.

if_from_as(STRING) --> space, "AS", space, string(NAME), {concat_atom(['AS: ', NAME], STRING)}.
if_from_as("") --> "".

%===============================================================================
% RUN
run(LINE) --> "RUN", space, string(COMMAND), new_line, {concat_atom(["RUN: ", COMMAND], LINE)}.

% TODO: opracować
% RUN ["dotnet", "restore"]
% run(LINE) --> "RUN", space, "[", string(COMMAND), "]" new_line, {concat_atom(["RUN: ", COMMAND], LINE)}.

%===============================================================================
% CMD
cmd(LINE) --> "CMD", space, string(COMMAND), new_line, {concat_atom(["CMD: ", COMMAND], LINE)}.

% TODO: opracować
% CMD ["/usr/lib/postgresql/9.3/bin/postgres", "-D", "/var/lib/postgresql/9.3/main", "-c", "config_file=/etc/postgresql/9.3/main/postgresql.conf"]

%===============================================================================
% LABEL
% LABEL <key>=<value> <key>=<value> <key>=<value> ...
label(LINE) --> "LABEL", space, key_value_pairs(PAIR), new_line, {concat_atom(["LABEL: ", PAIR], LINE)}.

key_value_pairs(PAIRS) --> key_value_pair(PAIR), space, key_value_pairs(REST), {concat_atom([PAIR,' ', REST], PAIRS)}.
key_value_pairs(PAIRS) --> key_value_pair(PAIRS).
key_value_pairs('') --> "".

key_value_pair(PAIR) --> if_qoute_mark(_), string(KEY), if_qoute_mark(_), "=", if_qoute_mark(_), string(VALUE), if_qoute_mark(_), { concat_atom([KEY, '===', VALUE], PAIR) }.
key_value_pair(PAIR) --> string(KEY), "=", string(VALUE), { concat_atom([KEY, '===', VALUE], PAIR) }.

if_qoute_mark(STRING) --> "\"", {concat_atom(['"'], STRING)}.
if_qoute_mark("") --> "".



%===============================================================================
% EXPOSE
% EXPOSE 80/tcp
expose(LINE) --> "EXPOSE", space, int(PORT), if_expose_protocol(PROTOCOL), new_line, {concat_atom(["EXPOSE: ", PORT, PROTOCOL], LINE)}.

if_expose_protocol(STRING) --> "/", string(PROTOCOL), {concat_atom(['PROTOCOL: ', PROTOCOL], STRING)}.
if_expose_protocol("") --> "".

%===============================================================================
% ENV
% ENV PYTHONUNBUFFERED 1
% ENV PYTHONUNBUFFERED=1
env(LINE) --> "ENV", space, chars(KEY), space, "=", space, string(VALUE), new_line, {concat_atom(["ENV: ", KEY, "=", VALUE], LINE)}.
% TODO: opracowac przypisanie ze spacja

%===============================================================================
% ADD
% TODO: opracować

%===============================================================================
% COPY
% TODO: opracować

%===============================================================================
% ENTRYPOINT
% TODO: opracować

%===============================================================================
% VOLUME
% VOLUME ["/myvol"]
% VOLUME /myvol
volume(LINE) --> "VOLUME", space, "[\"", string(DIRECTORY), "\"]", new_line, {concat_atom(["VOLUME: ", DIRECTORY], LINE)}.
volume(LINE) --> "VOLUME", space, string(DIRECTORY), new_line, {concat_atom(["VOLUME: ", DIRECTORY], LINE)}.

%===============================================================================
% USER
% USER <user>[:<group>]
% USER <UID>[:<GID>]
user(LINE) --> "USER", space, string(USER), new_line, {concat_atom(["USER: ", USER], LINE)}.
% TODO: dodanie grupy?

%===============================================================================
% WORKDIR
% WORKDIR /path/to/workdir
workdir(LINE) --> "WORKDIR", space, string(PATH), new_line, {concat_atom(["WORKDIR: ", PATH], LINE)}.

%===============================================================================
% ARG
% TODO: to odpuszczam

%===============================================================================
% ONBUILD
% TODO: to odpuszczam

%===============================================================================
% STOPSIGNAL
stopsignal(LINE) --> "STOPSIGNAL", space, string(SIGNAL), new_line, {concat_atom(["STOPSIGNAL: ", SIGNAL], LINE)}.

%===============================================================================
% HEALTHCHECK
% TODO: to odpuszczam

%===============================================================================
% SHELL
% TODO: to odpuszczam



% ADD
% COPY

% FROM
% LABEL
% STOPSIGNAL
% USER
% VOLUME
% WORKDIR
% ONBUILD









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