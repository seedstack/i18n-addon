--
-- Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
--
-- This Source Code Form is subject to the terms of the Mozilla Public
-- License, v. 2.0. If a copy of the MPL was not distributed with this
-- file, You can obtain one at http://mozilla.org/MPL/2.0/.
--


    create table SEED_I18N_KEY (
       ID varchar2(255 char) not null,
        DESCRIPTION varchar2(255 char),
        OUTDATED number(1,0),
        primary key (ID)
    );

    create table SEED_I18N_KEY_TRANS (
       Key_ID varchar2(255 char) not null,
        translations_KEY_ID varchar2(255 char) not null,
        translations_LOCALE varchar2(255 char) not null,
        primary key (Key_ID, translations_KEY_ID, translations_LOCALE)
    );

    create table SEED_I18N_LOCALE (
       CODE varchar2(255 char) not null,
        DEFAULT_LOCALE number(1,0),
        ENGLISH_LANGUAGE varchar2(255 char),
        LANGUAGE varchar2(255 char),
        primary key (CODE)
    );

    create table SEED_I18N_TRANSLATION (
       KEY_ID varchar2(255 char) not null,
        LOCALE varchar2(255 char) not null,
        APPROXIMATE number(1,0),
        OUTDATED number(1,0),
        TRANSLATION varchar2(255 char),
        primary key (KEY_ID, LOCALE)
    );

    alter table SEED_I18N_KEY_TRANS 
       add constraint UK_ta4xejyl84hjv7fx1xk3igyi2 unique (translations_KEY_ID, translations_LOCALE);

    alter table SEED_I18N_KEY_TRANS 
       add constraint FKmu86aa2tn70mu6isua1l4prlv 
       foreign key (translations_KEY_ID, translations_LOCALE) 
       references SEED_I18N_TRANSLATION;

    alter table SEED_I18N_KEY_TRANS 
       add constraint FKb9x21ktjcpckc3wktrlyqed1c 
       foreign key (Key_ID) 
       references SEED_I18N_KEY;
