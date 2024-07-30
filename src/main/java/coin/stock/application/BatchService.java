package coin.stock.application;

import coin.stock.domain.TickerProperties;
import coin.stock.global.config.BatchConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BatchService {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job importTickerJob;

    public void saveTickers(List<TickerProperties> tickers) {
        // 새로운 ListItemReader를 생성하여 데이터를 설정합니다.
        ListItemReader<TickerProperties> reader = new ListItemReader<>(tickers);

        // JobParameters를 생성합니다.
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        // importTickerJob을 실행합니다.
        try {
            jobLauncher.run(importTickerJob, jobParameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}