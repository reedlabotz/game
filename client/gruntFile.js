module.exports = function(grunt) {

    grunt.loadNpmTasks('grunt-contrib-concat');
    grunt.loadNpmTasks('grunt-contrib-jshint');
    grunt.loadNpmTasks('grunt-contrib-uglify');
    grunt.loadNpmTasks('grunt-contrib-clean');
    grunt.loadNpmTasks('grunt-contrib-copy');
    grunt.loadNpmTasks('grunt-contrib-watch');
    grunt.loadNpmTasks('grunt-recess');
    grunt.loadNpmTasks('grunt-html2js');

    // Default task.
    grunt.registerTask('default', ['jshint','build']);
    grunt.registerTask('build', ['clean','html2js','concat','recess:build','copy:img','copy:vendor']);
    grunt.registerTask('release', ['prodconfig','clean','html2js','uglify','jshint','concat:index', 'recess:min','copy:img','copy:vendor']);

    // Print a timestamp (useful for when watching)
    grunt.registerTask('timestamp', function() {
        grunt.log.subhead(Date());
    });

    // Set distdir for prod
    grunt.registerTask('prodconfig', function() {
        grunt.config('distdir','../server/public');
    });

    // Project configuration.
    grunt.initConfig({
        distdir: 'public',
        pkg: grunt.file.readJSON('package.json'),
        banner:
        '/*! <%= pkg.title || pkg.name %> - v<%= pkg.version %> - <%= grunt.template.today("yyyy-mm-dd") %>\n' +
            '<%= pkg.homepage ? " * " + pkg.homepage + "\\n" : "" %>' + 
            ' * Copyright (c) <%= grunt.template.today("yyyy") %> <%= pkg.author %>;\n' + 
            ' * Licensed <%= _.pluck(pkg.licenses, "type").join(", ") %>\n */\n', 
        src: { 
            js: ['src/**/*.js', '<%= distdir %>/templates/**/*.js'], 
            jsWatch: ['src/**/*.js'], 
            html: ['src/index.html'], 
            tpl:  {
                app: ['src/app/**/*.tpl.html'] 
            }, 
            less: ['src/less/stylesheet.less'], // recess:build doesn't accept ** in its file patterns 
            lessWatch: ['src/less/**/*.less'] 
        }, 
        clean: ['<%= distdir %>/*'], 
        copy: { 
            img:  {
                files: [{ dest: '<%= distdir %>/img', src : '**', expand: true, cwd: 'src/img/' }] 
            },
            vendor: {
                files: [{ dest: '<%= distdir %>/js', src : '**', expand: true, cwd: 'vendor/' }]
            }
        }, 
        html2js: { 
            app:  {
                options:  {
                    base: 'src/app'
                } ,
                src: ['<%= src.tpl.app %>'] ,
                dest: '<%= distdir %>/templates/app.js' ,
                module: 'templates.app' 
            } 
        }, 
        concat:{ 
            dist: {
                options:  {
                    banner: "<%= banner %> "
                } ,
                src:['<%= src.js %>'] ,
                dest:'<%= distdir %>/<%= pkg.name %>.js' 
            }, 
            index:  {
                src: ['src/index.html'] ,
                dest: '<%= distdir %>/index.html' ,
                options:  {
                    process: true
                }
            }
        },
        uglify: {
            dist:{
                options: {
                    banner: "<%= banner %>"
                },
                src:['<%= src.js %>'],
                dest:'<%= distdir %>/<%= pkg.name %>.js'
            }
        },
        recess: {
            build: {
                files: {
                    '<%= distdir %>/<%= pkg.name %>.css':
                    ['<%= src.less %>'] },
                options: {
                    compile: true
                }
            },
            min: {
                files: {
                    '<%= distdir %>/<%= pkg.name %>.css': ['<%= src.less %>']
                },
                options: {
                    compress: true
                }
            }
        },
        watch:{
            all: {
                files:['<%= src.jsWatch %>', '<%= src.lessWatch %>', '<%= src.tpl.app %>', '<%= src.html %>'],
                tasks:['default','timestamp']
            },
            build: {
                files:['<%= src.jsWatch %>', '<%= src.lessWatch %>', '<%= src.tpl.app %>', '<%= src.html %>'],
                tasks:['build','timestamp']
            }
        },
        jshint:{
            files:['gruntFile.js', '<%= src.js %>'],
            options:{
                curly:true,
                eqeqeq:true,
                immed:true,
                latedef:true,
                newcap:true,
                noarg:true,
                sub:true,
                boss:true,
                eqnull:true,
                globals:{}
            }
        }
    });
};