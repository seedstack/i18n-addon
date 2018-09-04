---
title: "I18n"
addon: "I18n"
repo: "https://github.com/seedstack/i18n-addon"
author: Pierre THIROUIN
description: "Provides backend services and a Web UI to manage application locales and translations."
zones:
    - Addons
noMenu: true    
---

The i18n addon provides backend services and a Web UI to manage your application's locales and translations.<!--more-->

## Dependencies

If you only need to internationalize at server-side, add the following dependency: 

{{< dependency g="org.seedstack.addons.i18n" a="i18n-core" >}}

If you need to expose internationalization REST APIs for your frontend, use the following dependency instead:

{{< dependency g="org.seedstack.addons.i18n" a="i18n-rest" >}}

If you also need the W20 UI, use the following dependency instead:

{{< dependency g="org.seedstack.addons.i18n" a="i18n-w20" >}}

## Persistence

The i18n add-on uses JPA to store its data in a relational database. You need to configure its JPA unit named `seed-i18n-domain`.
See the ({{< ref "addons/jpa/index.md" >}}) for details about how to configure JPA units. An example could be:

```yaml
jdbc:
  datasources:
    myDataSource:
        provider: org.seedstack.jdbc.internal.datasource.HikariDataSourceProvider
        url: jdbc:hsqldb:mem:testdb1
jpa:
  units:
    seed-i18n-domain:
      datasource: myDataSource
```

## Security

All the REST APIs are secured with permissions. These permissions are bound to two default roles:

* `seed-i18n.reader` for read access
* `seed-i18n.translator` for read/write/delete access

You can map those i18n roles to your own realm roles. Consider the following users:

* `john` having the role `MYPROJECT.DEVELOPER`,
* `admin` having the role `MYPROJECT.ADMIN`.

You can map the i18n roles like this:

```yaml
security:
  roles:
    seed-i18n.reader: [ MYPROJECT.DEVELOPER, MYPROJECT.ADMIN ]
    seed-i18n.translator: [ MYPROJECT.ADMIN ]
```

{{% callout info %}}
It is possible to create more fine grained security roles using the provided permissions. Below is the list of available
permissions:

```plain
seed:i18n:locale:read
seed:i18n:locale:write
seed:i18n:locale:delete

seed:i18n:key:read
seed:i18n:key:write
seed:i18n:key:delete

seed:i18n:translation:read
seed:i18n:translation:write
seed:i18n:translation:delete
```
{{% /callout %}}

## Cache (Optional)

The add-on uses cache to improve i18n performances. By default, this cache does not need configuration,
but it is possible to change the configuration as follow:

```yaml
i18n:
  cache:
    initialSize: 2048
    maxSize: 8192
    concurrencyLevel: 32
```

## Backup/Restore

The "Manage Keys" interface provides CSV import/export functionality. The exported CSV file is in UTF-8 in order to support 
all the possible languages.  

## Configuration

The following i18n options can be specified:

{{% config p="i18n" %}}
```yaml
i18n:
  # If true, enables a fallback to the default language of the application (false by default)
  translationFallback: boolean
  
  # If true, missing translations will appear as [key.name] in the returned translations (true by default)
  allowMissingTranslations: boolean
  
  # Allows to specify additional custom local codes
  additionalLocales: (List<String>)
  
  # Cache options for translations
  cache:
    # Initial size of the cache (maxSize/4 by default)
    initialSize: (int)
    
    # Max size of the cache (8192 by default)
    maxSize: (int)
    
    # Maximum concurrent access to the cache (32 by default)
    concurrencyLevel: (int)
```
{{% /config %}}   

## Usage

### Locales

The i18n add-on stores application available locales and default locale. Available locales are the locales in which
the application is translated, i.e. available to users. The default locale is the "native language" of the application.
This locale will be used as starting locale for translations.

Locales can be managed with i18n administration interface or programmatically with the {{< java "org.seedstack.i18n.LocaleService" >}}.

```java
public class SomeClass {
    @Inject
    private LocaleService localeService;
}
```

### Localization

Localization is provided by the {{< java "org.seedstack.i18n.LocalizationService" >}} which allows to localize date, number, 
string and currency.

```java
public class SomeClass {
    @Inject
    private LocalizationService localizationService;
}
```

The {{< java "org.seedstack.i18n.LocalizationService" >}} allows to translate i18n keys in different locales using the `localize(String, String)` method.
This method will fallback on the parent locale if the required locale is not present.

```java
public class SomeClass {
    @Inject
    private LocalizationService localizationService;

    public void someMethod() {
        // Case 1: fr-BE translation is present
        localizationService.localize("fr-BE", "key1"); // -> "translation fr-BE"
        
        // Case 2: fr-BE translation is NOT present, but fr translation is present
        localizationService.localize("fr-BE", "key1"); // -> "translation fr"
        
        // Case 2: no translation present
        localizationService.localize("fr-BE", "key1"); // -> "[key]"
    }
}
```












