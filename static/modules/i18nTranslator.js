/*
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
define([
    'module',
    'require',
    '{angular}/angular',
    '{lodash}/lodash',
    '{dropzone}/dropzone-amd-module',
    '{w20-core}/modules/notifications',
    '{angular-sanitize}/angular-sanitize',
    '{angular-resource}/angular-resource',
    '{w20-core}/modules/security',
    '{w20-dataviz}/modules/charts/multibar'
], function (_module, require, angular, _, Dropzone) {
    'use strict';

    var _config = _module && _module.config() || {};

    var module = angular.module('i18nTranslator', ['ngResource', 'ngSanitize', 'ui.bootstrap', 'ui.bootstrap.tooltip', 'w20CoreSecurity']);

    /**
     *  Target : Service to share the target language to be translated
     *  from the default language.
     */
    module.factory('Target', function () {
        var target;
        return {
            setTarget: function (lang) {
                target = lang;
                window.localStorage.setItem('target', JSON.stringify(lang));
            },
            getTarget: function () {
                target = JSON.parse(window.localStorage.getItem('target'));
                return target;
            }
        };
    });

    /**
     *  SeedI18nService : Service that manages REST resources.
     *  allLocales : return all the locales available
     *  applicationLocales : return all the locales that have been defined by the user
     *  defualtLocale : return the default/active locale of the application
     *  keys : return the keys
     *  translations : return the translations
     */
    module.factory('SeedI18nService', ['$resource', function ($resource) {
        return {
            allLocales: $resource(_config.seedi18nRestPrefix + '/locales'),

            applicationLocales: $resource(_config.seedi18nRestPrefix + '/available-locales/:code', {}, {
                update: {
                    method: 'PUT',
                    isArray: true
                }
            }),

            defaultLocale: $resource(_config.seedi18nRestPrefix + '/default-locale', {}, {
                update: {
                    method: 'PUT'
                }
            }),

            keys: $resource(_config.seedi18nRestPrefix + '/keys/:name', {}, {
                update: {
                    method: 'PUT',
                    params: {
                        name: '@name'
                    }
                },
                deleteAll: {
                    method: 'DELETE'
                }
            }),

            translations: $resource(_config.seedi18nRestPrefix + '/translations/:locale/:name', {
                locale: '@locale',
                name: '@name'
            }, {
                update: {
                    method: 'PUT'
                }
            }),

            statistics: $resource(_config.seedi18nRestPrefix + '/statistic')
        };
    }]);


    /**
     * Statistic controller
     */
    module.controller('SeedI18nStatisticController',
        ['$scope', 'SeedI18nService', '$location', 'Target', 'NotificationService', 'AuthorizationService', 'AuthenticationService',
            function ($scope, SeedI18nService, $location, Target, notificator, authorizationService, authenticationService) {

                $scope.authorization = authorizationService;
                $scope.authentication = authenticationService;

                $scope.applicationLocales = [];
                $scope.allLocales = [];
                $scope.show = false;
                $scope.hide = false;
                //default select option for language combox "ALL"
                $scope.selectLang = '';

                $scope.goTo = function (location) {
                    if ($scope.allowTranslate()) {
                        $location.path(location);
                    }
                };

                // Fetching the application locales
                SeedI18nService.applicationLocales.query(function (applicationLocales) {
                    $scope.applicationLocales = applicationLocales;

                    // ...followed by all the available locales (change to a ".then()" promise instead of callback with angular 1.2)
                    SeedI18nService.allLocales.query(function (allLocales) {
                        // Filtering the list of all locales  by removing the one that are already
                        // in the application locales list so that they don't repeat.
                        $scope.allLocales = difference(allLocales, $scope.applicationLocales);
                    });
                    // Setting the default locale. The view won't show if data couldn't be fetched successfully.
                    SeedI18nService.defaultLocale.get(function (defaultLocale) {
                            for (var i = 0; i < $scope.applicationLocales.length; i++) {
                                if (defaultLocale.englishLanguage === $scope.applicationLocales[i].englishLanguage) {
                                    $scope.defaultLocale = $scope.applicationLocales[i];
                                    break;
                                }
                            }
                            $scope.show = true;
                        },
                        function () {
                            $scope.hide = true;
                        });
                });


                // If the list of application locales changes, we set another language as the
                // default target language because the target language could have been removed.
                $scope.$watch('applicationLocales', function () {
                    $scope.target = $scope.applicationLocales[0];
                });


                // allow translate only if there is at least 2 locales and a default locale
                $scope.allowTranslate = function () {
                    return !!($scope.applicationLocales.length > 1 && typeof $scope.defaultLocale !== 'undefined');
                };


                //when load the statistic page
                SeedI18nService.statistics.query({selectLang: $scope.selectLang},
                    function (data) {
                        showStatisticResults(data);
                        $scope.statisticTableData = data;
                    });

                //when the language combobox in tab table do a select
                $scope.changeLangTable = function () {
                    SeedI18nService.statistics.query({selectLang: $scope.selectLang},
                        function (data) {
                            //showStatisticResults(data);
                            $scope.statisticTableData = data;
                        });
                };

                //when click the tab multibar
                $scope.redrawMultibar = function () {
                    SeedI18nService.statistics.query({selectLang: $scope.selectLang},
                        function (data) {
                            showStatisticResults(data);
                        });
                };

                //when click the tab table
                $scope.redrawTable = function () {
                    SeedI18nService.statistics.query({selectLang: $scope.selectLang},
                        function (data) {
                            $scope.statisticTableData = data;
                        });
                };

                //when the language combobox in tab Multibar do a select
                $scope.changeLangMultibar = function () {
                    SeedI18nService.statistics.query({selectLang: $scope.selectLang},
                        function (data) {
                            showStatisticResults(data);
                        });
                };

                //to show the statistic results in multibar
                function showStatisticResults(data) {
                    if (data.length) {
                        //set statistic table data
                        //$scope.statisticTableData = data;

                        //change format from list to mutlibar's data format
                        //data translated
                        var data_tld = [];
                        //data to translate
                        var data_tt = [];
                        for (var i = 0; i < data.length; i++) {
                            var tld = [];
                            tld.push(data[i].locale);
                            tld.push(data[i].translated);
                            var tt = [];
                            tt.push(data[i].locale);
                            tt.push(data[i].totranslate);
                            data_tld.push(tld);
                            data_tt.push(tt);
                        }
                        var obj = {};
                        obj.key = 'to translate';
                        obj.values = data_tt;
                        var obj1 = {};
                        obj1.key = 'translated';
                        obj1.values = data_tld;
                        var mData = [];
                        mData.push(obj);
                        mData.push(obj1);
                        $scope.multibarData = mData;

                        //multibar config
                        $scope.multibarConfig = {
                            data: $scope.multibarData,
                            yAxisShowMaxMin: true,
                            xAxisShowMaxMin: false,
                            showLegend: true,
                            showControls: true,
                            tooltips: true,
                            staggerLabels: true,
                            yAxisLabel: 'Nb of key',
                            xAxisLabel: 'Language'
                        };
                    } else {
                        $scope.statisticTableData = [];
                    }
                }


                // return index of object in "myArray" with "searchterm" as the value of "property"
                function arrayObjectIndexOf(myArray, searchTerm, property) {
                    for (var i = 0, len = myArray.length; i < len; i++) {
                        if (myArray[i][property] === searchTerm) {
                            return i;
                        }
                    }
                    return -1;
                }

                // substract array two from one
                function difference(one, two) {
                    var result = [];
                    for (var i = 0, len1 = one.length; i < len1; i++) {
                        if (arrayObjectIndexOf(two, one[i].englishLanguage, 'englishLanguage') === -1) {
                            result.push(one[i]);
                        }
                    }
                    return result;
                }

            }]);// end controller


    /**
     * Dashboard controller
     */
    module.controller('SeedI18nDashboardController',
        ['$scope', 'SeedI18nService', '$location', 'Target', 'NotificationService', 'AuthorizationService', 'AuthenticationService',
            function ($scope, SeedI18nService, $location, Target, notificator, authorizationService, authenticationService) {

                $scope.authorization = authorizationService;
                $scope.authentication = authenticationService;

                $scope.applicationLocales = [];
                $scope.allLocales = [];
                $scope.show = false;
                $scope.hide = false;

                $scope.goTo = function (location) {
                    if ($scope.allowTranslate()) {
                        $location.path(location);
                    }
                };

                // Fetching the application locales
                SeedI18nService.applicationLocales.query(function (applicationLocales) {
                    $scope.applicationLocales = applicationLocales;
                    // ...followed by all the available locales (change to a ".then()" promise instead of callback with angular 1.2)
                    SeedI18nService.allLocales.query(function (allLocales) {
                        // Filtering the list of all locales  by removing the one that are already
                        // in the application locales list so that they don't repeat.
                        $scope.allLocales = difference(allLocales, $scope.applicationLocales);
                    });
                    // Setting the default locale. The view won't show if data couldn't be fetched successfully.
                    SeedI18nService.defaultLocale.get(function (defaultLocale) {
                            for (var i = 0; i < $scope.applicationLocales.length; i++) {
                                if (defaultLocale.englishLanguage === $scope.applicationLocales[i].englishLanguage) {
                                    $scope.defaultLocale = $scope.applicationLocales[i];
                                    break;
                                }
                            }
                            $scope.show = true;
                        },
                        function () {
                            $scope.hide = true;
                        });
                });


                // If the list of application locales changes, we set another language as the
                // default target language because the target language could have been removed.
                $scope.$watch('applicationLocales', function () {
                    $scope.target = $scope.applicationLocales[0];
                });

                // Add locales to the set of application locale
                $scope.add = function () {
                    if ($scope.allLangSelected) {
                        $scope.applicationLocales = $scope.applicationLocales.concat($scope.allLangSelected);
                    }
                    // remove the locale from the list of all locales
                    $scope.allLocales = difference($scope.allLocales, $scope.applicationLocales);
                    //$scope.languageToTranslate = $scope.applicationLocales[0]; !! REVERT
                    $scope.updateApplicationLocales();
                    $scope.allLangSelected = '';
                };

                // remove locales

                $scope.remove = function () {
                    if ($scope.availLangSelected) {
                        $scope.applicationLocales = difference($scope.applicationLocales, $scope.availLangSelected);
                        $scope.allLocales = $scope.allLocales.concat($scope.availLangSelected);

                        // if the default locale was in the set of application locale it becomes undefined
                        if (typeof $scope.defaultLocale !== 'undefined') {
                            if (arrayObjectIndexOf($scope.applicationLocales, $scope.defaultLocale.englishLanguage, 'englishLanguage') < 0) {
                                $scope.defaultLocale = undefined;
                            }
                        }

                        Target.setTarget('');

                        angular.forEach($scope.availLangSelected, function (localToDelete) {
                            SeedI18nService.applicationLocales.delete({code: localToDelete.code}, function () {
                                    notificator.notify('Local deleted : ' + localToDelete.code);
                                },
                                function () {
                                    notificator.notify('Failed delete local');
                                });
                        });
                    }
                    $scope.availLangSelected = '';
                };

                // allow translate only if there is at least 2 locales and a default locale
                $scope.allowTranslate = function () {
                    return !!($scope.applicationLocales.length > 1 && typeof $scope.defaultLocale !== 'undefined');
                };

                // Update the default local
                $scope.updateDefault = function () {
                    SeedI18nService.defaultLocale.update($scope.defaultLocale);
                };

                // Update the visible application locales (when removing, adding etc.)
                $scope.updateApplicationLocales = function () {
                    //  if ($scope.applicationLocales.length)
                    SeedI18nService.applicationLocales.update($scope.applicationLocales, function (data) {
                    });
                };

                // return index of object in "myArray" with "searchterm" as the value of "property"
                function arrayObjectIndexOf(myArray, searchTerm, property) {
                    for (var i = 0, len = myArray.length; i < len; i++) {
                        if (myArray[i][property] === searchTerm) {
                            return i;
                        }
                    }
                    return -1;
                }

                // substract array two from one
                function difference(one, two) {
                    var result = [];
                    for (var i = 0, len1 = one.length; i < len1; i++) {
                        if (arrayObjectIndexOf(two, one[i].englishLanguage, 'englishLanguage') === -1) {
                            result.push(one[i]);
                        }
                    }
                    return result;
                }

            }]);// end controller

    /**
     * Manage key controller
     */
    module.controller('SeedI18nKeysController',
        ['$scope', 'SeedI18nService', '$timeout', 'Target', '$location', 'NotificationService', 'AuthorizationService', 'AuthenticationService',
            function ($scope, SeedI18nService, $timeout, Target, $location, notificator, authorizationService, authenticationService) {

                $scope.authorization = authorizationService;
                $scope.authentication = authenticationService;

                $scope.restPrefix = _config.seedi18nRestPrefix;
                $scope.importKey = false;
                $scope.addKey = false; // boolean to show/hide "add key" button
                $scope.keys = []; // to hold all the keys
                $scope.keysMissing = []; // to hold all the keys with "missing" property (i.e keys empty)
                $scope.keysOutdated = []; // to hold all the keys with "outdated" property (a key is outdated if the default translation has changed but not the other translations accordingly, could have been called "keysStale" as well)
                $scope.currentKey = []; // to hold the selected key
                $scope.disableKeyInput = false; // disable key textarea if no default locale

                //Pagination
                $scope.pageIndex = 1;
                $scope.pageSize = 10;
                $scope.maxSize = 5;

                $scope.goTo = function (location) {
                    if ($scope.allowTranslate()) {
                        $location.path(location);
                    }
                };

                /**
                 * Update the list of keys depending on the parameters
                 * @param pageIndex  the page index for the pagination
                 * @param pageSize  number of items required per page
                 * @param missing  if true filter missing keys (return missing keys, not the other unless another filter is specified)
                 * @param outdated if true filter outdated keys (return outdated keys, not the other unless another filter is specified)
                 * @param searchName text string to filter keys on
                 * @param approx if true filter approximate keys (return approximate keys, not the other unless another filter is specified)
                 */
                $scope.updateKeysList = function (pageIndex, pageSize, missing, outdated, searchName, approx) {
                    SeedI18nService.keys.query({
                            pageIndex: pageIndex,
                            pageSize: pageSize,
                            isMissing: missing,
                            isOutdated: outdated,
                            searchName: searchName,
                            isApprox: approx
                        },
                        function (data) {
                            if (data.length) {
                                $scope.keys = data;

                                // the if is for the pagination, if we are not on the
                                // last element we set the the first key of the page to be selected
                                if (!$scope.lastOfList) {
                                    $scope.setCurrentKey($scope.keys[0]);
                                }
                                // else we set the last element as selected
                                else {
                                    $scope.setCurrentKey($scope.keys[$scope.keys.$viewInfo.pageSize - 1]);
                                    $scope.lastOfList = false;
                                }

                                $scope.numPages = function () {
                                    // note .$viewInfo metadata holder property is added to the prototype by an $http interceptor outside of this module
                                    return Math.ceil(data.$viewInfo.resultSize / $scope.pageSize);
                                };
                            } else {
                                $scope.keys = [];
                            }

                        });
                };

                /**
                 * Delete keys depending on the parameters
                 */
                $scope.deleteFiltered = function () {
                    if (!$scope.keys.length) {
                        window.alert('There is no filtered keys.');
                        return;
                    }
                    if (window.confirm('Are you sure you want to delete all ' +
                        'the currently filtered keys ? Only the filtered ' +
                        'keys will be deleted. This action is irreversible.')) {
                        SeedI18nService.keys.delete({
                                isMissing: $scope.missing,
                                isOutdated: $scope.outdated,
                                searchName: $scope.tempFilterText
                            },
                            function (data) {
                                notificator.notify(data);
                                $scope.updateKeysList(0, $scope.pageSize, $scope.missing, $scope.outdated, $scope.tempFilterText);
                            }, function (response) {
                                notificator.notify(response.status);
                            });
                    }
                };

                // Update the list whenever page and/or filter checkboxes change
                $scope.$watch('pageIndex + pageSize + outdated + missing', function () {
                    $scope.hideDelete = true;
                    $scope.updateKeysList($scope.pageIndex - 1, $scope.pageSize, $scope.missing, $scope.outdated);
                });

                // get the default locale
                SeedI18nService.defaultLocale.get(function (defaultLocale) {
                    if (typeof defaultLocale.englishLanguage === 'undefined') {
                        $scope.disableKeyInput = true;
                    }
                    $scope.defaultLocale = defaultLocale;
                    //get the application locales
                    SeedI18nService.applicationLocales.query(function (data) {
                        $scope.applicationLocales = data;
                        $scope.target = $scope.applicationLocales[1];
                        $scope.allowTranslate = function () {
                            return typeof $scope.defaultLocale.englishLanguage !== 'undefined' && $scope.applicationLocales.length > 1;
                        };
                    });
                });

                $scope.setTarget = function (target) {
                    $scope.target = target;
                };

                $scope.translate = function () {
                    Target.setTarget($scope.target);
                    if ($scope.target) {
                        if ($scope.defaultLocale.englishLanguage !== $scope.target.englishLanguage) {
                            $location.path('seed-i18n/translate');
                        }
                        else {
                            notificator.notify('default locale is already ' + $scope.defaultLocale.englishLanguage);
                        }
                    }
                };


                /**
                 * Set the current key and watch its properties in order to dynamically set the "state" (outdated, approximate, missing)
                 * @param key the key to set as current
                 */
                $scope.setCurrentKey = function (key) {
                    // set when a key was deleted to hide the right panel where the key was. It goes back to false again
                    // when setting a new current key
                    $scope.hideDelete = false;
                    var backendValue;
                    SeedI18nService.keys.get({name: key.name}, function (data) {
                        backendValue = data;
                        key.comment = backendValue.comment;
                        key.translation = backendValue.translation;
                        $scope.currentKey[0] = key;
                        // Used to set the square "outdated" and "missing" by watching the state of the currentKey
                        $scope.$watch('currentKey[0]', function (newVal, oldVal) {
                            if (newVal.name === oldVal.name) {
                                if (newVal.translation !== oldVal.translation) {
                                    $scope.currentKey[0].outdated = true;
                                }
                                if ($scope.currentKey[0].translation === backendValue.translation && !backendValue.outdated) {
                                    $scope.currentKey[0].outdated = false;
                                }
                                $scope.currentKey[0].missing = !newVal.translation;
                            }
                        }, true);
                    });
                };

                // search filter with .5s throttle before it fetches result from the backend
                $scope.tempFilterText = '';
                var filterTextTimeout;
                $scope.$watch('keyFilterSearch.name', function (val) {
                    if (filterTextTimeout) {
                        $timeout.cancel(filterTextTimeout);
                    }
                    $scope.tempFilterText = val;
                    filterTextTimeout = $timeout(function () {
                        $scope.updateKeysList(0, $scope.pageSize, $scope.missing, $scope.outdated, $scope.tempFilterText);
                    }, 500);
                });

                $scope.clear = function () {
                    if (window.confirm('Clear form ?')) {
                        $scope.currentKey[0].comment = '';
                        $scope.currentKey[0].translation = '';
                    }

                };

                $scope.navigate = function (direction) {
                    for (var i = 0, len = $scope.keys.length; i < len; i++) {
                        if ($scope.keys[i].name === $scope.currentKey[0].name) {
                            if (direction === 'backward') {
                                if (i !== 0) {
                                    $scope.setCurrentKey($scope.keys[i - 1]);
                                }
                                else if ($scope.pageIndex > 1) {
                                    $scope.lastOfList = true;
                                    $scope.pageIndex--;
                                }
                                break;
                            }
                            if (direction === 'forward') {
                                if (i !== $scope.keys.length - 1) {
                                    $scope.setCurrentKey($scope.keys[i + 1]);
                                }
                                else if ($scope.pageIndex !== $scope.keys.$viewInfo.pagesCount) {
                                    $scope.lastOfList = false;
                                    $scope.pageIndex++;
                                }
                                break;
                            }


                        }
                    }
                };

                /**
                 * Update the key
                 * @param key the key to update
                 */
                $scope.updateKeys = function (key) {
                    SeedI18nService.keys.update({name: key.name}, key, function () {
                            notificator.notify('key saved');
                        },
                        function () {
                            notificator.notify('key failed to save');
                        });
                };

                $scope.newKey = {name: ''};

                // Watch the model on input field of new key to prevent space input in key title
                $scope.$watch('newKey.name', function () {
                    if ($scope.newKey.name) {
                        $scope.newKey.name = $scope.newKey.name.toLowerCase().replace(/\s+/g, '');
                    }
                });

                /**
                 * Save a new key
                 * @param key the new key to save
                 * @param createAnother flag to create another key
                 */
                $scope.submitKey = function (key, createAnother) {
                    // add defaultLocale to new key :
                    key.defaultLocale = $scope.defaultLocale.code;
                    var newkey = new SeedI18nService.keys(key);
                    SeedI18nService.keys.save(newkey, function (data) {
                        $scope.keys.push(data);
                        notificator.notify('new key saved');
                        $scope.newKey.name = '';
                        $scope.newKey.comment = '';
                        $scope.newKey.translation = '';
                        // close panel if not "save and new"
                        if (!createAnother) {
                            $timeout(function () {
                                $scope.addKey = !$scope.addKey;
                            }, 1000);
                        }
                    }, function (response) {
                        if (response.status === 400) {
                            notificator.notify(response.data);
                        }
                        if (response.status === 409) {
                            notificator.notify('Failed to save : key name already exist.');
                        }
                    });
                };

                $scope.resetNewKey = function () {
                    if (window.confirm('Clear form ?')) {
                        $scope.newKey.name = '';
                        $scope.newKey.comment = '';
                        $scope.newKey.translation = '';
                    }
                };

                $scope.deleteKey = function (key) {
                    if (window.confirm('Delete Key ?')) {
                        SeedI18nService.keys.delete({name: key.name}, key, function (data) {
                            $scope.keys.splice(_.indexOf($scope.keys, _.find($scope.keys, function (k) {
                                return k.name === data.name;
                            })), 1);
                        });

                        $scope.hideDelete = true;
                    }
                };

                $scope.exportPath = $scope.restPrefix + '/keys/file';


            }]);

    /**
     * Translations controller
     */
    module.controller('SeedI18nTranslateController',
        ['$scope', '$location', 'SeedI18nService', 'Target', '$timeout', '$route', 'NotificationService', 'AuthorizationService', 'AuthenticationService',
            function ($scope, $location, seedI18nService, Target, $timeout, $route, notificator, authorizationService, authenticationService) {

                $scope.authorization = authorizationService;
                $scope.authentication = authenticationService;

                $scope.noTarget = false;

                $scope.goTo = function (location) {
                    $location.path(location);
                };

                function getTranslations(locale, pageIndex, pageSize, missing, outdated, approx, searchName, callback) {
                    seedI18nService.translations.query(
                        {
                            locale: locale,
                            pageIndex: pageIndex,
                            pageSize: pageSize,
                            isMissing: missing,
                            isOutdated: outdated,
                            isApprox: approx,
                            searchName: searchName
                        },

                        function (data) {
                            if (data.length) {
                                $scope.keys = data;

                                if (!$scope.lastOfList) {
                                    $scope.setCurrentKey($scope.keys[0]);
                                }
                                else {
                                    $scope.setCurrentKey($scope.keys[$scope.keys.$viewInfo.pageSize - 1]);
                                    $scope.lastOfList = false;
                                }

                                $scope.numPages = function () {
                                    return Math.ceil(data.$viewInfo.resultSize / $scope.pageSize);
                                };
                                // TODO : implement percentage completed ( best to implement dataviz charts though )
                                /*var total = data.$viewInfo.resultSize;
                                 $scope.percentComplete = function() {
                                 if (total == 0)
                                 return ('No key to translate');
                                 var untranslated =  $scope.keysMissing.length;
                                 var translated = total - untranslated;

                                 return (Math.round(translated * 100 / total) +  ' %');
                                 }*/
                            } else {
                                $scope.keys = [];
                            }
                            if (callback) {
                                callback();
                            }
                        });
                }


                function doTranslate() {
                    $scope.keys = [];
                    $scope.currentKey = [];

                    //Pagination
                    $scope.pageIndex = 1;
                    $scope.pageSize = 10;
                    $scope.maxSize = 5;


                    $scope.$watch('pageIndex + pageSize + outdated + missing + approx', function () {
                        getTranslations($scope.target.code, $scope.pageIndex - 1, $scope.pageSize, $scope.missing, $scope.outdated, $scope.approx);
                    });

                    // search
                    $scope.tempFilterText = '';
                    var filterTextTimeout;
                    $scope.$watch('keyFilterSearch.name', function (val) {
                        if (filterTextTimeout) {
                            $timeout.cancel(filterTextTimeout);
                        }
                        $scope.tempFilterText = val;
                        filterTextTimeout = $timeout(function () {
                            getTranslations($scope.target.code, 0, $scope.pageSize, $scope.missing, $scope.outdated, $scope.approx, $scope.tempFilterText);
                        }, 500);
                    });
                    /**
                     * Set current key, watch its state to update it dynamically
                     * @param key
                     */
                    $scope.setCurrentKey = function (key) {
                        var backendValue;
                        seedI18nService.translations.get({locale: $scope.target.code, name: key.name}, function (data) {
                            backendValue = data;
                            key.comment = backendValue.comment;
                            key.source.translation = backendValue && backendValue.source && backendValue.source.translation || '';
                            key.target.translation = backendValue && backendValue.target && backendValue.target.translation || '';
                            $scope.currentKey[0] = key;
                            $scope.saveSuccess = false;
                            $scope.$watch('currentKey[0]', function (newVal, oldVal) {
                                if (newVal.name === oldVal.name) {
                                    if (newVal.target.translation !== oldVal.target.translation) {
                                        $scope.currentKey[0].target.outdated = false;
                                    }

                                    if (($scope.currentKey[0].target.translation === backendValue.target.translation) &&
                                        backendValue.target.outdated && !$scope.currentKey[0].missing) {
                                        $scope.currentKey[0].target.outdated = true;
                                    }

                                    $scope.currentKey[0].missing = !newVal.target.translation;
                                }

                            }, true);

                        });
                    };

                    $scope.save = function (key, goNext) {
                        seedI18nService.translations.update({name: key.name, locale: $scope.target.code}, key,
                            function () {
                                notificator.notify('Save success');
                            },
                            function () {
                                notificator.notify('Failed to save');
                            });
                        if (goNext) {
                            $scope.navigate('forward');
                        }
                    };

                    $scope.navigate = function (direction) {
                        for (var i = 0, len = $scope.keys.length; i < len; i++) {
                            if ($scope.keys[i].name === $scope.currentKey[0].name) {
                                if (direction === 'backward') {
                                    if (i !== 0) {
                                        $scope.setCurrentKey($scope.keys[i - 1]);
                                    }
                                    else if ($scope.pageIndex > 1) {
                                        $scope.lastOfList = true;
                                        $scope.pageIndex--;
                                    }
                                    break;
                                }
                                if (direction === 'forward') {
                                    if (i !== $scope.keys.length - 1) {
                                        $scope.setCurrentKey($scope.keys[i + 1]);
                                    }
                                    else if ($scope.pageIndex !== $scope.keys.$viewInfo.pagesCount) {
                                        $scope.lastOfList = false;
                                        $scope.pageIndex++;
                                    }
                                    break;
                                }
                            }
                        }
                    };

                    $scope.reset = function () {
                        if (window.confirm('Clear form ?')) {
                            $scope.currentKey[0].target.translation = '';
                        }

                    };
                }


                seedI18nService.defaultLocale.get(function (defaultLocale) {
                    if (typeof defaultLocale.englishLanguage === 'undefined') {
                        $scope.noTarget = true;
                    } else {
                        $scope.defaultLocale = defaultLocale;
                        $scope.allowTranslate = $scope.defaultLocale; // !
                        seedI18nService.applicationLocales.query(function (data) {
                            if (!data.length || data.length < 2) {
                                $scope.noTarget = true;
                            } else {
                                $scope.applicationLocales = data;
                                $scope.applicationLocales = _.filter($scope.applicationLocales, function (locale) {
                                    return locale.englishLanguage !== $scope.defaultLocale.englishLanguage;
                                });
                                $scope.target = Target.getTarget(); // get the target language
                                if (!$scope.target) {
                                    $scope.target = $scope.applicationLocales[0];
                                }
                                $scope.noTarget = false; // Tells if their is a target language to translate into
                                doTranslate();
                            }
                        });
                    }
                });

                $scope.setTarget = function (target) {
                    Target.setTarget(target);
                    $route.reload();
                };


            }

        ]);

    /** Directives **/

        // show a label for notifying key saves, delete etc.
    module.directive('notification', function () {
        return {
            restrict: 'A',
            replace: true,
            scope: {
                text: '@',
                type: '@',
                show: '='
            },
            template: '<div class="label label-{{type}} pull-right" data-ng-show="show">{{text}}</div>'
        };
    });

    module.directive('dropZone', function () {
        return function (scope) {
            var myDropzone = new Dropzone('#file-dropzone', {
                url: scope.restPrefix + '/keys/file',
                maxFilesize: 10000,
                paramName: 'file',
                maxThumbnailFilesize: 100,
                uploadMultiple: true,
                //acceptedFiles: 'text/csv',
                addRemoveLinks: true,
                removedfile: function (file) {
                    var _ref;
                    return (_ref = file.previewElement) !== null ? _ref.parentNode.removeChild(file.previewElement) : void 0;
                },
                autoProcessQueue: false,
                init: function () {
                    var submitButton = window.document.querySelector('#submit-all');
                    myDropzone = this; // closure
                    submitButton.addEventListener('click', function () {
                        myDropzone.processQueue();
                    });
                    this.on('addedfile', function () {

                    });
                    this.on('success', function (file, response) {
                        file.serverId = response;
                    });
                },
                uploadprogress: function (file, progress) {
                    var node, _i, _len, _ref, _results;
                    _ref = file.previewElement.querySelectorAll('[data-dz-uploadprogress]');
                    _results = [];
                    for (_i = 0, _len = _ref.length; _i < _len; _i++) {
                        node = _ref[_i];
                        // show a 50% completed progress when upload complete successfully
                        _results.push(node.style.width = '' + progress / 2 + '%');
                    }
                    return _results;
                },
                success: function (file) {
                    var node, _i, _len, _ref, _results;
                    _ref = file.previewElement.querySelectorAll('[data-dz-uploadprogress]');
                    _results = [];
                    for (_i = 0, _len = _ref.length; _i < _len; _i++) {
                        node = _ref[_i];
                        // show 100% completed progress on success
                        _results.push(node.style.width = '' + 100 + '%');
                    }
                    // refresh keys page
                    scope.updateKeysList(scope.pageIndex - 1, scope.pageSize, scope.missing, scope.outdated);
                    return file.previewElement.classList.add('dz-success');
                }
            });

        };
    });


    // Expose the angular module to W20 loader
    return {
        angularModules: ['i18nTranslator']
    };
});



