/*
 * Copyright 2014-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.retrocode.web;

import com.retrocode.MarksStore;
import com.retrocode.MarksStoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

import static org.springframework.data.geo.Metrics.KILOMETERS;
import static org.springframework.data.geo.Metrics.MILES;


@Controller
@RequiredArgsConstructor
class MarksStoresController {

	private static final List<Distance> DISTANCES = Arrays.asList(new Distance(1, KILOMETERS), new Distance(5, KILOMETERS),new Distance(10, KILOMETERS),new Distance(100, KILOMETERS));
	private static final Distance DEFAULT_DISTANCE = new Distance(100, Metrics.KILOMETERS);
	private static final Map<String, Point> KNOWN_LOCATIONS;
    private static final List<String> provinces;

	static {

		Map<String, Point> locations = new HashMap<>();

		locations.put("Calgary", new Point(-114.0086693, 50.9177282));
		locations.put("Vancouver", new Point(-123.1207, 49.2827));

		KNOWN_LOCATIONS = Collections.unmodifiableMap(locations);


        provinces = Arrays.asList("British Columbia","Alberta","Saskatchewan","Manitoba");


	}

	private final MarksStoreRepository repository;
	private final RepositoryEntityLinks entityLinks;

	/**
	 * Looks up the stores in the given distance around the given location.
	 * 
	 * @param model the {@link Model} to populate.
	 * @param location the optional location, if none is given, no search results will be returned.
	 * @param distance the distance to use, if none is given the {@link #DEFAULT_DISTANCE} is used.
	 * @param pageable the pagination information
	 * @return
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	String index(Model model, @RequestParam Optional<Point> location, @RequestParam Optional<Distance> distance,
			Pageable pageable) {

		Point point = location.orElse(KNOWN_LOCATIONS.get("Calgary"));

		Page<MarksStore> stores = repository.findByAddressLocationNear(point, distance.orElse(DEFAULT_DISTANCE), pageable);

		model.addAttribute("stores", stores);
		model.addAttribute("distances", DISTANCES);
		model.addAttribute("selectedDistance", distance.orElse(DEFAULT_DISTANCE));
		model.addAttribute("location", point);
		model.addAttribute("locations", KNOWN_LOCATIONS);
        model.addAttribute("province","");
        //model.addAttribute("provinces",provinces);
		model.addAttribute("api", entityLinks.linkToSearchResource(MarksStore.class, "by-location", pageable).getHref());

		return "index";
	}

	@RequestMapping(value = "/province", method = RequestMethod.GET)
	String index2(Model model, @RequestParam Optional<String> province,
				 Pageable pageable) {

		String province2Use = province.orElse("Alberta");

		Page<MarksStore> stores = repository.findByAddressProvince(province2Use, pageable);

		model.addAttribute("stores", stores);
        model.addAttribute("distances", DISTANCES);
        model.addAttribute("selectedDistance", (DEFAULT_DISTANCE));
        model.addAttribute("location", KNOWN_LOCATIONS.get("Calgary"));
        model.addAttribute("locations", KNOWN_LOCATIONS);
        model.addAttribute("province",province2Use);
        //model.addAttribute("provinces",provinces);
		model.addAttribute("api", entityLinks.linkToSearchResource(MarksStore.class, "by-province", pageable).getHref());

		return "index";
	}
}
