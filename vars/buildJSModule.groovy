def call(String APP_NAME) {
    node {
        ansiColor('xterm') {
            timestamps {
                try {
                    def playBook = libraryResource("deploy_js.yml")
                    def hosts = libraryResource("hosts")
                    def ansibleCfg = libraryResource("ansible.cfg")
                    stage('Checkout') {
                        println "Выкачиваем репозиторий с исходным кодом"
                        git_checkout = checkout scm
                        println git_checkout
                    }
                    stage("Собираем build ${APP_NAME}") {
                        sh "npm install && npm run build"
                    }
                    stage('Деплоим и запускаем build') {
                        writeFile file: 'playbook.yml', text: playBook
                        writeFile file: 'hosts', text: hosts
                        writeFile file: 'ansible.cfg', text: ansibleCfg
                        ansiblePlaybook(
                                credentialsId: 'private_key',
                                inventory: "hosts",
                                playbook: "deploy_js.yml",
                                colorized: true
                        )
                    }
                } catch(exception){
                    println "Сборка упала: " + "\n${exception}"
                    currentBuild.result = 'FAILURE'
                }
                finally {
                    cleanWs()
                }
            }
        }
    }
}
