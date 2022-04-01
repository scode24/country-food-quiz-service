package com.quiz.security.configuration;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Random;

import org.springframework.context.annotation.Configuration;

import com.quiz.model.Options;

@Configuration
public class OptionsConfiguration {

	Options options;
	List<Options> optionsList;
	EnumMap<Options, Integer> optionMap = new EnumMap<>(Options.class);

	public OptionsConfiguration() {
		optionsList = Arrays.asList(Options.values());
	}

	public String[] getOptionList(String correctOption) {
		String[] options = new String[3];
		options[0] = convertToTitleCase(correctOption);
		options[1] = convertToTitleCase(getNextOption(new String[] { options[0] }, optionsList));
		options[2] = convertToTitleCase(getNextOption(new String[] { options[0], options[1] }, optionsList));
		shuffleOptions(options);
		return options;
	}

	private String convertToTitleCase(String correctOption) {
		correctOption = correctOption.toLowerCase();
		String newStr = "";
		boolean isUppercaseNeeded = true;
		for (int i = 0; i < correctOption.length(); i++) {
			if (isUppercaseNeeded) {
				newStr += correctOption.substring(i, i + 1).toUpperCase();
				isUppercaseNeeded = false;
			} else if (correctOption.substring(i, i + 1).equalsIgnoreCase("_")
					|| correctOption.substring(i, i + 1).equalsIgnoreCase(" ")) {
				newStr += " ";
				isUppercaseNeeded = true;
			} else {
				newStr += correctOption.substring(i, i + 1);
			}
		}
		return newStr;
	}

	private String getNextOption(String[] existingOptions, List<Options> optionList) {
		Random random = new Random();
		int newIndex = random.nextInt(optionList.size() - 1 - 0) + 0;

		if (existingOptions.length == 1
				&& !optionsList.get(newIndex).name().replaceAll("_", " ").equals(existingOptions[0])) {
			return optionsList.get(newIndex).name();
		}

		if (existingOptions.length == 2
				&& !optionsList.get(newIndex).name().replaceAll("_", " ").equals(existingOptions[0])
				&& !optionsList.get(newIndex).name().replaceAll("_", " ").equals(existingOptions[1])) {
			return optionsList.get(newIndex).name();
		}

		return getNextOption(existingOptions, optionList);
	}

	private void shuffleOptions(String[] a) {
		int n = a.length;
		Random random = new Random();
		random.nextInt();

		for (int i = 0; i < n; i++) {
			int change = i + random.nextInt(n - i);
			String holder = a[i];
			a[i] = a[change];
			a[change] = holder;
		}
	}
}
