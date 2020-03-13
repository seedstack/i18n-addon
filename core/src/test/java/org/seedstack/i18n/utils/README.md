#### SchemaGenerator usage

SchemaGenerator is intended to generate DDL scripts for various database vendors, based on annotations in following classes:

- Translation
- TranslationId
- Locale
- Key

Run from Intellij, the scripts are generated under `sql` directory at the root of i18n-addon project. 
If files are already present in this directory (e.g. scripts from previous version), DDL statements will be appended to the existing files. 

> Existing DDL scripts should be removed manually before running the tool.