package coin.stock.global.config;

import coin.stock.domain.TickerProperties;
import coin.stock.entity.Ticker;
import coin.stock.repository.TickerRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Configuration
public class BatchConfig {

    @Bean
    public Job importTickerJob(JobRepository jobRepository, PlatformTransactionManager transactionManager, ListItemReader<TickerProperties> reader, TickerRepository tickerRepository) {
        return new JobBuilder("importTickerJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(step1(jobRepository, transactionManager, reader, tickerRepository))
                .build();
    }

    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager, ListItemReader<TickerProperties> reader, TickerRepository tickerRepository) {
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
        return tickers -> tickerRepository.saveAll(tickers);
    }
}