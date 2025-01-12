package faang.school.paymentservice.config;

import com.atomikos.icatch.jta.UserTransactionManager;
import com.atomikos.jdbc.AtomikosDataSourceBean;
import jakarta.transaction.SystemException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
public class AtomikosConfiguration {
    @Bean(name = "postgresDataSource")
    public DataSource postgresDataSource() {
        AtomikosDataSourceBean ds = new AtomikosDataSourceBean();
        ds.setUniqueResourceName("postgresDataSource");
        ds.setXaDataSourceClassName("org.postgresql.xa.PGXADataSource");

        // Укажите параметры подключения к вашей базе данных
        ds.setXaProperties(getPostgresProperties());

        ds.setPoolSize(5); // Укажите размер пула соединений
        return ds;
    }

    private Properties getPostgresProperties() {
        Properties properties = new Properties();
        properties.setProperty("user", "user"); // ваш логин
        properties.setProperty("password", "password"); // ваш пароль
        properties.setProperty("databaseName", "postgres"); // имя базы данных
        properties.setProperty("serverName", "localhost"); // адрес сервера
        properties.setProperty("portNumber", "5432");
        return properties;
    }

    @Bean(initMethod = "init", destroyMethod = "close")
    public UserTransactionManager userTransactionManager() {
        return new UserTransactionManager();
    }

}
