/*
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/* global module: false, grunt: false, process: false */
module.exports = function (grunt) {
    'use strict';

    grunt.initConfig({
        pkg: grunt.file.readJSON('package.json'),
        clean: [
            'bower_components/**',
            'dist/**',
	        'coverage/**'
        ],
        jshint: {
            static: {
                src: ['static/modules/**/*.js']
            }
        },
        bower: {
            install: {
                options: {
                    copy: false
                }
            }
        },
        concat : {
            static: {
                options : {
                    sourceMap :true
                },
                dist : {
                    src  : ['static/modules/**/*.js'],
                    dest : '.tmp/static-concat.js'
                }
            }
        },
        uglify : {
            static: {
                options : {
                    sourceMap : true,
                    sourceMapIncludeSources : true,
                    sourceMapIn : '<%= concat.static.dist.dest %>.map'
                },
                dist : {
                    src  : '<%= concat.static.dist.dest %>',
                    dest : 'dist/i18n-function.min.js'
                }
            }
        }
    });

    grunt.loadNpmTasks('grunt-contrib-jshint');
    grunt.loadNpmTasks('grunt-contrib-clean');
    grunt.loadNpmTasks('grunt-contrib-concat');
    grunt.loadNpmTasks('grunt-contrib-uglify');
    grunt.loadNpmTasks('grunt-bower-task');

    grunt.registerTask('default', ['jshint', 'bower', 'concat', 'uglify']);
} ;
