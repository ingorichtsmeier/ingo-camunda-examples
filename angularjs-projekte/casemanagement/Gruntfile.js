'use strict';

module.exports = function(grunt) {

  require('load-grunt-tasks')(grunt);


  // project configuration
  grunt.initConfig({
    pkg: grunt.file.readJSON('package.json'),

    config: {
      sources: 'client',
      dist: 'dist/case-ui/app/case/default',
      commonsUiDir: 'node_modules/camunda-commons-ui'
    },

    jshint: {
      src: [
        ['<%=config.sources %>']
      ],
      options: {
        jshintrc: true
      }
    },

    browserify: {
      options: {
        browserifyOptions: {
          // strip unnecessary built-ins
          builtins: [ 'events' ],
          insertGlobalVars: {
            process: function () {
                return 'undefined';
            },
            Buffer: function () {
                return 'undefined';
            }
          }
        },
        transform: [ 'brfs' ]
      },
      watch: {
        options: {
          watch: true
        },
        files: {
          '<%= config.dist %>/js/app.js': [ '<%= config.sources %>/**/*.js' ]
        }
      },
      app: {
        files: {
          '<%= config.dist %>/js/app.js': [ '<%= config.sources %>/**/*.js' ]
        }
      }
    },
    copy: {
      app: {
        files: [
          {
            expand: true,
            cwd: '<%= config.sources %>/app/case/default',
            src: ['**/*.*', '!**/*.js'],
            dest: '<%= config.dist %>'
          },
          {
            expand: true,
            cwd: '<%= config.commonsUiDir %>/vendor/fonts',
            src: ['*.{eot,svg,ttf,woff,woff2}'],
            dest: '<%= config.dist %>/fonts/'
          },
          {
            expand: true,
            cwd: '<%= config.commonsUiDir %>/node_modules/bootstrap/fonts',
            src: ['*.{eot,svg,ttf,woff,woff2}'],
            dest: '<%= config.dist %>/fonts/'
          },
          {
            expand: true,
            cwd: '<%= config.commonsUiDir %>/node_modules/bpmn-font/dist/font',
            src: ['*.{eot,svg,ttf,woff,woff2}'],
            dest: '<%= config.dist %>/fonts/'
          }
        ]
      }
    },
    less: {
      options: {
        dumpLineNumbers: 'comments',
        paths: [
          'node_modules','node_modules/camunda-commons-ui','node_modules/camunda-commons-ui/node_modules'
        ]
      },

      styles: {
        files: {
          '<%= config.dist %>/css/app.css': 'styles/app.less'
        }
      }
    },
    watch: {
      options: {
        livereload: true
      },
      samples: {
        files: [ '<%= config.sources %>/**/*.*' ],
        tasks: [ 'copy:app' ]
      },
      less: {
        files: [
          'styles/**/*.less',
          'node_modules/camunda-commons-ui/resources/less/**/*.less'
        ],
        tasks: [
          'less'
        ]
      },
    },
    connect: {
      livereload: {
        options: {
          port: 9013,
          livereload: true,
          hostname: 'localhost',
          open: false,
          base: [
            'dist'
          ]
        }
      }
    },
    war: {
      target: {
        options: {
          war_dist_folder: 'install',    /* Folder where to generate the WAR. */
          war_name: 'case-ui'                    /* The name fo the WAR file (.war will be the extension) */
        },
        files: [
          {
            expand: true,
            cwd: 'dist/case-ui/',
            src: ['**'],
            dest: ''
          }
        ]
      }
    }
  });

  // tasks

  grunt.registerTask('build', [ 'browserify:app', 'copy:app', 'war' ]);

  grunt.registerTask('auto-build', [
    'copy',
    'browserify:watch',
    'connect',
    'less',
    'watch'
  ]);

  grunt.registerTask('default', [ 'jshint', 'build' ]);
};
