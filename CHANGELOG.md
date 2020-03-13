# Version 4.2.0 (2020-04-30)

* [chg] Updated for Seedstack 20.4.
* [fix] Updated DDL scripts. 

# Version 4.1.0 (2017-08-01)

* [chg] Merged W20 frontend code directly into the `w20` module. 

# Version 4.0.0 (2017-11-30)

* [chg] Updated for business framework 4.0.

# Version 3.0.0 (2016-12-19)

* [chg] Updated to new configuration system.

# Version 2.2.2 (2016-07-29)

* [chg] Frontend updated to [2.1.4](https://github.com/seedstack/w20-i18n-addon/releases/tag/v2.1.4)

# Version 2.2.1 (2016-04-28)

* [new] Configuration property `org.seedstack.i18n.translation-fallback` enables translation fallback to default language (disabled by default).
* [chg] Frontend updated to [2.1.3](https://github.com/seedstack/w20-i18n-addon/releases/tag/v2.1.3)
* [chg] Updated for SeedStack 16.4.
* [brk] Requires Jersey 2.

# Version 2.2.0 (2016-01-21)

* [chg] Moved translations of i18n itself to its frontend as static files
* [chg] Vastly better performance on first load and in subsequent uses
* [brk] Simplified database mapping, no longer compatible with tables of previous versions.
* [fix] CSV import will no longer add null translations (no more NPE on import)

# Version 2.1.1 (2015-12-04)

* [brk] The locale codes are now store with the standard format, e.g. es-AR (and no more es_AR).
* [fix] CSV file import
* [fix] Localization for locales with language + country
* [new] Add the possibility to display the missing translation with the format [key.name]
* [new] Simplify the configuration for additional locales

# Version 2.1.0 (2015-11-26)

* [chg] Refactored as an add-on and updated to work with Seed 2.1.0+
* [chg] Separated the W20 client in its [own repository](https://github.com/seedstack/w20-i18n-addon).

# Version 2.0.0 (2015-07-30)

* [new] Initial Open-Source release.
