package coin.stock.global.config;

import coin.stock.domain.TickerProperties;
import coin.stock.entity.Ticker;
import coin.stock.repository.TickerRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Collections;

@Configuration
public class BatchConfig {

    private final TickerRepository tickerRepository;

    public BatchConfig(TickerRepository tickerRepository) {
        this.tickerRepository = tickerRepository;
    }

    @Bean
    public Job importTickerJob(JobRepository jobRepository, PlatformTransactionManager transactionManager, ListItemReader<TickerProperties> reader) {
        return new JobBuilder("importTickerJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(step1(jobRepository, transactionManager, reader))
                .build();
    }

    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager, ListItemReader<TickerProperties> reader) {
        return new StepBuilder("step1", jobRepository)
                .<TickerProperties, Ticker>chunk(100, transactionManager)
                .reader(reader)
                .processor(processor())
                .writer(writer(tickerRepository))
                .build();
    }

    @Bean
    public ListItemReader<TickerProperties> reader() {
        return new ListItemReader<>(Collections.emptyList());
    }

    @Bean
    public ItemProcessor<TickerProperties, Ticker> processor() {
        return tickerProperties -> Ticker.builder()
                .market(tickerProperties.market())
                .tradePrice(tickerProperties.tradePrice())
                .accTradePrice24h(tickerProperties.accTradePrice24h())
                .build();
    }

    @Bean
    public ItemWriter<Ticker> writer(TickerRepository tickerRepository) {
        return tickerRepository::saveAll;
    }
}