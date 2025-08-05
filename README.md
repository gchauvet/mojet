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
  <version>m.n.p</version>
</dependency>
```

## 🧪 Example POJO Usage
```java
@Record
public class ChildPojo {

    @Fragment(length = 6)
    private double total;
    @Fragment(length = 3, alignement = PadWay.LEFT)
    private String label;
    @Fragment(length = 5)
    @Occurences(3)
    private long[] values;

    // Getters / Setters...
}

@Record
@Zap(length = 5, value = '_') // Adds 5 underscores at the end of each record (optional)
public class SimplePojo implements RecordVisitable {

    @Fragment(length = 7, padder = '0')
    private long id; // Number padded to 7 characters with '0'

    @Zap(length = 3, value = '#')
    @Zap(length = 2, value = '|')
    @Fragment(length = 10)
    private String name; // Left-padded to 10 characters with spaces

    @Fragment(length = 10, padder = '_', alignement = PadWay.RIGHT)
    private String surname; // Right-padded to 10 characters with underscores

    @Transform(YearMonthTypeHandler.class)
    @Fragment(length = 4, format = "uuMM")
    private YearMonth date; // Formatted as 2-digit year and 2-digit month (e.g. "2506")

    @Fragment(length = 2, padder = '$')
    private byte octet = 5;

    @Fragment(length = 2, padder = '€')
    private char car = 'C';

    @Fragment(length = 8, optional = true)
    private LocalDate optional; // Optional field; if unparsable or empty, set to null

    @Record
    private ChildPojo child; // append fragments/fillers description to main mapper description

    // Getters / Setters...

    @Override
    public void accept(RecordVisitor visitor) {
        visitor.visit(this);
    }
}
```

## ☕ Spring Batch Integration Example

```java
@Configuration
@EnableBatchProcessing
public class ExampleBatchConfig {

    private final NodeBuilder builder = new NodeBuilder();

    @Bean
    public FlatFileItemReader<SimplePojo> reader() {
        FlatFileItemReader<SimplePojo> reader = new FlatFileItemReader<>();
        reader.setResource(new ClassPathResource("input.txt"));
        reader.setLineMapper(new MojetLineMapper<>(builder, SimplePojo.class));
        return reader;
    }

    @Bean
    public FlatFileItemWriter<SimplePojo> writer() {
        FlatFileItemWriter<SimplePojo> writer = new FlatFileItemWriter<>();
        writer.setResource(new FileSystemResource("output.txt"));
        writer.setLineAggregator(new MojetLineAggregator<>(builder, SimplePojo.class));
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

## 📊 Multi-Record Type Support Example

Mojet seamlessly integrates with Spring Batch to handle flat files containing multiple record types (e.g., header, detail, footer lines identified by a prefix). Use `PatternMatchingCompositeLineMapper` to delegate mapping based on line patterns.

### Example POJOs for Multiple Types
To handle the different record types in a type-safe manner, use the Visitor pattern. The POJOs implement `RecordVisitable`, allowing them to be visited by a `RecordVisitor`.

First, define a `PojoVisitor` implementation:

```java
public interface PojoVisitor extends RecordVisitor {
    void visit(HeaderPojo header);
    void visit(DetailPojo detail);
    void visit(FooterPojo footer);
}
```

And pojo classes:
```java
@Record
@Matcher("H*")
public class HeaderPojo implements RecordVisitable<PojoVisitor> {

    @Fragment(length = 1)
    private String type; // 'H'

    @Fragment(length = 10, format = "yyyyMMdd")
    private LocalDate date;

    // Getters / Setters...

    @Override
    public void accept(PojoVisitor visitor) {
        visitor.visit(this);
    }
}

@Record
@Matcher("D*")
public class DetailPojo implements RecordVisitable<PojoVisitor> {

    @Fragment(length = 1)
    private String type; // 'D'

    @Fragment(length = 7, padder = '0')
    private long id;

    @Fragment(length = 10)
    private String name;

    @Fragment(length = 6)
    private double amount;

    // Getters / Setters...

    @Override
    public void accept(PojoVisitor visitor) {
        visitor.visit(this);
    }
}

@Record
@Matcher("F*")
public class FooterPojo implements RecordVisitable<PojoVisitor> {

    @Fragment(length = 1)
    private String type; // 'F'

    @Fragment(length = 5)
    private int recordCount;

    @Fragment(length = 10)
    private double totalAmount;

    // Getters / Setters...

    @Override
    public void accept(PojoVisitor visitor) {
        visitor.visit(this);
    }
}
```

### Using the Visitor Pattern for Type-Specific Processing

```java
public class MyRecordVisitor implements RecordVisitor {

    @Override
    public void visit(HeaderPojo header) {
        // Handle header logic, e.g., log date
        System.out.println("Header date: " + header.getDate());
    }

    @Override
    public void visit(DetailPojo detail) {
        // Handle detail logic, e.g., accumulate amount
        System.out.println("Detail name: " + detail.getName() + ", amount: " + detail.getAmount());
    }

    @Override
    public void visit(FooterPojo footer) {
        // Handle footer logic, e.g., validate total
        System.out.println("Footer record count: " + footer.getRecordCount());
    }
}
```

Then, create an `ItemProcessor` that uses the visitor.
This approach allows for clean, type-specific processing of each record type using the Visitor pattern.

## 🛠 Roadmap
* ✅ Annotation-based POJO-to-fixed-length mapping (including arrays)
* ✅ Multi-filler and padding support
* ✅ Custom formatting support (e.g. LocalDate)
* ✅ Multi-record type support
* ❌ Collections not yet supported
* ❌ Inheritance not yet supported

## 🚀 Contributing
Contributions are welcome!

* Fork the repo
* Create your feature branch: git checkout -b feature/my-feature
* Commit your changes: git commit -am 'Add new feature'
* Push to the branch: git push origin feature/my-feature
* Open a Pull Request