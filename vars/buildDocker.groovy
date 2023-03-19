def call(String appName) {
    DOCKER_REGISTRY_REPO = "alimzhannurshinov"
    node {
        ansiColor('xterm') {
            timestamps {
                try {
                    stage('Checkout') {
                        println "Выкачиваем репозиторий с исходным кодом"
                        git_checkout = checkout scm
                        println git_checkout
                    }
                    stage('Build') {
                        println "Собираем образ и пушим в registry"
                        branch = git_checkout.branch == "master" ? "latest" : git_checkout.branch
                        sh "docker build -t ${DOCKER_REGISTRY_REPO}/${appName.toLowerCase()}:${branch}."
                        sh "docker push ${DOCKER_REGISTRY_REPO}/${appName.toLowerCase()}:${branch}"
                    }
                    cleanWs()
                } catch (exception) {
                    println "Сборка упала: " + "\n${exception}"
                    currentBuild.result = 'FAILURE'
                } finally {
                    cleanWs()
                }
            }
        }
    }
}