# Mojet: an extended spring-batch bean wrapper
[![Java CI](https://github.com/gchauvet/mojet/actions/workflows/maven.yml/badge.svg)](https://github.com/gchauvet/mojet/actions/workflows/maven.yml) [![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=gchauvet_mojet&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=gchauvet_mojet) [![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=gchauvet_mojet&metric=security_rating)](https://sonarcloud.io/dashboard?id=gchauvet_mojet) [![Coverage](https://sonarcloud.io/api/project_badges/measure?project=gchauvet_mojet&metric=coverage)](https://sonarcloud.io/dashboard?id=gchauvet_mojet)

**Mojet** is a wrapper around Spring Batch’s `BeanWrapperFieldSetMapper` and `BeanWrapperFieldExtractor` that makes it easy to map POJOs to and from fixed-length flat files using **annotations**.

This library is built to simplify reading and writing structured flat files in Spring Batch without having to write custom mappers or line aggregators.

---

## ✨ Features

- Declarative mapping via annotations
- Padding and filler support (left/right, custom characters)
- Custom field formatting (e.g. `LocalDate`)
- Works with Spring Batch’s `FlatFileItemReader` and `FlatFileItemWriter`
- Plug-and-play integration with Spring Batch

---

## 📦 Maven Dependency
(Not published yet: need build locally)
```xml
<dependency>
  <groupId>pro.cyberyon</groupId>
  <artifactId>mojet</artifactId>
  <version>1.0.0</version>
</dependency>
```
## 🧪 Example POJO Usage
```java
@Record
public class ChildPojo {

    @Fragment(length = 6)
    private double total;
    @Fragment(length = 3)
    private String label;
    @Fragment(length = 5)
    @Occurences(3)
    private long[] values;

    // Getters / Setters...
}

@Record
@Filler(length = 5, value = '_') // Adds 5 underscores at the end of each record (optional)
public class SimplePojo {

    @Fragment(length = 7, padder = '0')
    private long id; // Number padded to 7 characters with '0'

    @Filler(length = 3, value = '#')
    @Filler(length = 2, value = '|')
    @Fragment(length = 10)
    private String name; // Left-padded to 10 characters with spaces

    @Fragment(length = 10, padder = '_', alignement = PadWay.RIGHT)
    private String surname; // Right-padded to 10 characters with underscores

    @Converter(MyLocalDateTypeHandler.class)
    @Fragment(length = 4, format = "uuMM")
    private LocalDate date; // Formatted as 2-digit year and 2-digit month (e.g. "2506")

    @Fragment(length = 2, padder = '$')
    private byte octet = 5;

    @Fragment(length = 2, padder = '€')
    private char car = 'C';

    @Record
    private ChildPojo child; // append fragments/fillers description to main mapper description

    // Getters / Setters...
}
```

## ☕ Spring Batch Integration Example

```java
@Configuration
@EnableBatchProcessing
public class ExampleBatchConfig {

    @Bean
    public FlatFileItemReader<SimplePojo> reader() {
        FlatFileItemReader<SimplePojo> reader = new FlatFileItemReader<>();
        reader.setResource(new ClassPathResource("input.txt"));
        reader.setLineMapper(new MojetLineMapper<>(SimplePojo.class));
        return reader;
    }

    @Bean
    public FlatFileItemWriter<SimplePojo> writer() {
        FlatFileItemWriter<SimplePojo> writer = new FlatFileItemWriter<>();
        writer.setResource(new FileSystemResource("output.txt"));
        writer.setLineAggregator(new MojetLineAggregator<>(SimplePojo.class));
        return writer;
    }

    @Bean
    public Step step1(StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory.get("step1")
            .<SimplePojo, SimplePojo>chunk(10)
            .reader(reader())
            .writer(writer())
            .build();
    }

    @Bean
    public Job job(JobBuilderFactory jobBuilderFactory) {
        return jobBuilderFactory.get("importUserJob")
            .incrementer(new RunIdIncrementer())
            .flow(step1(null))
            .end()
            .build();
    }
}
```

## 🛠 Roadmap
* ✅ Annotation-based POJO-to-fixed-length mapping (including arrays)
* ✅ Multi-filler and padding support
* ✅ Custom formatting support (e.g. LocalDate)
* 🧪 Multi-record type support (in progress)
* ❌ Collections not yet supported

## 🚀 Contributing
Contributions are welcome!

* Fork the repo
* Create your feature branch: git checkout -b feature/my-feature
* Commit your changes: git commit -am 'Add new feature'
* Push to the branch: git push origin feature/my-feature
* Open a Pull Request
