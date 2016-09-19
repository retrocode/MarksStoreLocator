
package com.retrocode;


import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.separator.DefaultRecordSeparatorPolicy;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;

import com.retrocode.MarksStore.Address;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


@Slf4j
@Component
public class StoreInitializer {

	@Autowired
	public StoreInitializer(MarksStoreRepository repository, MongoOperations operations) throws Exception {

		if (repository.count() != 0) {
			return;
		}

		List<MarksStore> stores = readStores();
		System.out.println("Importing " + stores.size() + " stores into MongoDB…");
		repository.save(stores);

	}


	public static List<MarksStore> readStores() throws Exception {

		ClassPathResource resource = new ClassPathResource("marks_stores.csv");
		Scanner scanner = new Scanner(resource.getInputStream());
		String line = scanner.nextLine();
		scanner.close();

		FlatFileItemReader<MarksStore> itemReader = new FlatFileItemReader<MarksStore>();
		itemReader.setResource(resource);

		// DelimitedLineTokenizer defaults to comma as its delimiter
		DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
		tokenizer.setNames(line.split(","));
		tokenizer.setStrict(false);

		DefaultLineMapper<MarksStore> lineMapper = new DefaultLineMapper<MarksStore>();
		lineMapper.setFieldSetMapper(fields -> {

			Point location = new Point(fields.readDouble("Longitude"), fields.readDouble("Latitude"));
			Address address = new Address(fields.readString("Address1"), fields.readString("City"),fields.readString("Postal"),fields.readString("Province"),location);

			return new MarksStore(fields.readString("Name"), fields.readString("Number"),address);
		});

		lineMapper.setLineTokenizer(tokenizer);
		itemReader.setLineMapper(lineMapper);
		itemReader.setRecordSeparatorPolicy(new DefaultRecordSeparatorPolicy());
		itemReader.setLinesToSkip(1);
		itemReader.open(new ExecutionContext());

		List<MarksStore> stores = new ArrayList<>();
		MarksStore store = null;

		do {

			store = itemReader.read();

			if (store != null) {
				stores.add(store);
			}

		} while (store != null);

		return stores;
	}
}
