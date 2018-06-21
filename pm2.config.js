module.exports = {
    apps: [
        {
            name: 'ranking',
            args: [
                "-jar",
                "build/libs/jalgoarena-ranking-2.1.0-SNAPSHOT.jar"
            ],
            script: 'java',
            env: {
                PORT: 5006,
                BOOTSTRAP_SERVERS: 'localhost:9092,localhost:9093,localhost:9094'
            }
        }
    ]
};
