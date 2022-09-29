def call() {
    node {
        ansiColor('xterm') {
            timestamps {
                try {
                    def playBook = libraryResource("deploy_app.yml")
                    def hosts = libraryResource("hosts")
                    def ansibleCfg = libraryResource("ansible.cfg")
                    stage('Checkout') {
                        println "Выкачиваем репозиторий с исходным кодом"
                        git_checkout = checkout scm
                        println git_checkout
                    }
                    stage('Build') {
                        println "Собираем"
                        sh "go build -o online_shop"
                        binPath = sh(script: "pwd", returnStdout: true).trim().toString() + "/online_shop"
                    }
                    stage('Deploy') {
                        withEnv(["ANSIBLE_CONFIG=ansible.cfg"]) {
                            println "Заливаем через ансибл"
                            writeFile file: 'playbook.yml', text: playBook
                            writeFile file: 'hosts', text: hosts
                            writeFile file: 'ansible.cfg', text: ansibleCfg
                            ansiblePlaybook(
                                    credentialsId: 'private_key',
                                    inventory: "hosts",
                                    playbook: "playbook.yml",
                                    colorized: true
                            )
                        }
                    }
                    cleanWs()
                } catch (exception) {
                    println "Сборка упала: " + "\n${exception}"
                    currentBuild.result = 'FAILURE'
                    cleanWs()
                }
            }
        }
    }
}