package course_planning;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class CoursePlanningOriginal {
	class APTest implements Comparable<APTest> {
		public String apTest;
		public int score;

		public APTest(String apTest, int score) {
			this.apTest = apTest;
			this.score = score;
		}

		public int compareTo(APTest other) {
			int comp1 = this.apTest.compareTo(other.apTest);
			int comp2 = Integer.compare(this.score, other.score);
			return (comp1 == 0) ? (comp2) : (comp1);
		}

		@Override
		public String toString() {
			return this.apTest + " -> " + score;
		}
	}
	class Course implements Comparable<Course> {
		public String courseID, subject;
		public int year, creditHours, uniqueID;

		public Course(String courseID) {
			this.courseID = courseID;
			int indexOfSpace = courseID.indexOf(' ');
			this.subject = courseID.substring(0, indexOfSpace);
			this.year = courseID.charAt(indexOfSpace + 1) - '0';
			this.creditHours = courseID.charAt(indexOfSpace + 2) - '0';
			this.uniqueID = Integer.parseInt(courseID.substring(indexOfSpace + 3));
		}

		public int compareTo(Course other) {
			int comp1 = Integer.compare(this.year, other.year);
			int comp2 = Integer.compare(this.creditHours, other.creditHours);
			int comp3 = Integer.compare(this.uniqueID, other.uniqueID);
			int comp4 = this.subject.compareTo(other.subject);
			return (comp1 == 0) ? ((comp2 == 0) ? ((comp3 == 0) ? (comp4) : (comp3))  : (comp2)) : (comp1);
		}

		@Override
		public String toString() {
			return this.courseID;
		}
	}

	public BufferedReader file;
	public PrintWriter out;
	public TreeMap<APTest, ArrayList<TreeSet<Course>>> testToCourses;
	public TreeMap<Course, TreeSet<Course>> coursePre, courseCo, graph;
	public TreeMap<Course, Integer> coDegreeIn, preDegreeIn;
	public ArrayList<ArrayList<TreeSet<Course>>> bypass;
	public PriorityQueue<Course> coursesAbleToTake;
	public TreeSet<Course> requiredCourses, preParents, coParents;
	public Comparator<Course> compareCourse;
	public ArrayList<Course> updateAfterSemester;

	public static void main(String[] args) throws IOException {
		new CoursePlanningOriginal().run();
	}

	public void run() throws IOException {
		// Create the mapping of AP Test + Score to Course(s).
		this.initTestToCourses();

		// Create I/O objects
		file = new BufferedReader(new InputStreamReader(System.in));
		out = new PrintWriter(System.out);

		// Take in the major
		String major = file.readLine();

		// Take in the list of required courses
		requiredCourses = new TreeSet<Course>();
		String[] courseIDs = file.readLine().split(",");
		Arrays.asList(courseIDs).stream().map(str -> new Course(str)).collect(Collectors.toList()).stream().forEach(course -> requiredCourses.add(course));

		// Take in the AP scores for a given student and generate the list of classes bypassed
		this.readAPScores();

		// Take in the pre- and co-requisites for a given degree.
		this.readPreCorequisites();

		// Close Input object
		file.close();

		// Perform the topological sort
		this.topologicalSort();

		// Close output object
		out.close();
	}

	public void topologicalSort() {
		// Set up all of the objects that are needed to perform the sort
		this.topologicalSortInit();

		// Handle all of the processing for existing credit
		this.handleExistingCredit();

		// Get the list of courses that can be taken from the start
		this.getStartingOptions();

		// Perform the topological sort
		int semester = 1;
		while(!requiredCourses.isEmpty() && !coursesAbleToTake.isEmpty()) {
			// This will dictate the beginning of a semester
			TreeSet<Course> coursesForSemester = new TreeSet<Course>();
			int hoursRemainingForSemester = 16;
			updateAfterSemester = new ArrayList<Course>();

			// Choose the courses for the current semester
			TreeSet<Course> coursesToAddBack = new TreeSet<Course>();
			while(!coursesAbleToTake.isEmpty()) {
				// Get the current course
				Course curr = coursesAbleToTake.poll();
				// If we actually care about this course
				if(requiredCourses.contains(curr)) {
					// If we can take the current course
					if(hoursRemainingForSemester - curr.creditHours >= 0) {
						// Generate what would be the lab course object
						Course lecture = new Course(this.getLectureSection(curr.courseID));
						// If the course has a lab period (and isn't itself the lab period)...
						if(requiredCourses.contains(lecture) && !curr.courseID.equals(lecture.courseID)) {
							// If we can take both the lab and the lecture...
							if(hoursRemainingForSemester - (curr.creditHours + lecture.creditHours) >= 0) {
								// Add the courses to the semester
								coursesForSemester.add(curr);
								coursesForSemester.add(lecture);

								// Subtract the hours of the courses from the total remaining
								hoursRemainingForSemester -= (curr.creditHours + lecture.creditHours);

								// Update the lecture's children's in-degree
								graph.get(curr).stream().forEach(child -> {
									// If the parent is the child's co-requisite...
									if(courseCo.containsKey(child) && courseCo.get(child).contains(curr)) {
										// Then we can update this now
										coDegreeIn.put(child, coDegreeIn.get(child)-1);
										// If the total in-degree is now zero...
										if(coDegreeIn.get(child) + preDegreeIn.get(child) == 0) {
											// Then we can go ahead and add it to the courses that can be searched
											coursesAbleToTake.add(child);
										}
									} 
									// Otherwise the relationship is that of a prerequisite...
									else {
										// Then we need to change the values after the semester...
										updateAfterSemester.add(child);
									}
								});

								// Update the lecture's children's in-degree
								graph.get(lecture).stream().forEach(child -> {
									// If the parent is the child's co-requisite...
									if(courseCo.containsKey(child) && courseCo.get(child).contains(lecture)) {
										// Then we can update this now
										coDegreeIn.put(child, coDegreeIn.get(child)-1);
										// If the total in-degree is now zero...
										if(coDegreeIn.get(child) + preDegreeIn.get(child) == 0) {
											// Then we can go ahead and add it to the courses that can be searched
											coursesAbleToTake.add(child);
										}
									} 
									// Otherwise the relationship is that of a prerequisite...
									else {
										// Then we need to change the values after the semester...
										updateAfterSemester.add(child);
									}
								});

								// Remove the lab from the toSearch
								TreeSet<Course> keep = new TreeSet<Course>();
								while(!coursesAbleToTake.isEmpty()) {
									Course c = coursesAbleToTake.poll();
									if(c.compareTo(lecture) != 0) {
										keep.add(c);
									}
								}
								coursesAbleToTake.addAll(keep);

								// Remove both courses from the required courses
								requiredCourses.remove(curr);
								requiredCourses.remove(lecture);
							} else {
								// Otherwise, this is a pair that will need to be taken together another semester
								coursesToAddBack.add(curr);
								coursesToAddBack.add(lecture);
								// Remove the lab from the toSearch
								TreeSet<Course> keep = new TreeSet<Course>();
								while(!coursesAbleToTake.isEmpty()) {
									Course c = coursesAbleToTake.poll();
									if(c.compareTo(lecture) != 0) {
										keep.add(c);
									}
								}
								coursesAbleToTake.addAll(keep);
							}
						} else {
							// Add the course to the semester
							coursesForSemester.add(curr);

							// Subtract the hours of the courses from the total remaining
							hoursRemainingForSemester -= curr.creditHours;

							// Update the lecture's children's in-degree
							graph.get(curr).stream().forEach(child -> {
								// If the parent is the child's co-requisite...
								if(courseCo.containsKey(child) && courseCo.get(child).contains(curr)) {
									// Then we can update this now
									coDegreeIn.put(child, coDegreeIn.get(child)-1);
									// If the total in-degree is now zero...
									if(coDegreeIn.get(child) + preDegreeIn.get(child) == 0) {
										// Then we can go ahead and add it to the courses that can be searched
										coursesAbleToTake.add(child);
									}
								} 
								// Otherwise the relationship is that of a prerequisite...
								else {
									// Then we need to change the values after the semester...
									updateAfterSemester.add(child);
								}
							});

							// Remove the course from the required courses
							requiredCourses.remove(curr);
						}
					} else {
						coursesToAddBack.add(curr);
					}
				}
			}
			// Add the courses back that were chosen not to be taken this semester.
			coursesAbleToTake.addAll(coursesToAddBack);

			// Update the prerequisite relationships
			updateAfterSemester.stream().forEach(child -> {
				// Calculate the new degree
				int degree = preDegreeIn.get(child)-1;
				// Update the degree
				preDegreeIn.put(child, degree);
				// If the child has no more pre- or co-requisites...
				if(degree == 0 && coDegreeIn.get(child) == 0) {
					// Add the child to the list to be searched
					coursesAbleToTake.add(child);
				}
			});

			// Print the courses for the semester
			out.println("Semester " + semester + " - " + (16 - hoursRemainingForSemester) + " Credit Hours - " + (coursesForSemester.size()) + " Courses:");
			StringBuilder sb = new StringBuilder();
			coursesForSemester.stream().forEach(course -> {
				sb.append(course);
				sb.append(", ");
			});
			int lastIndex = sb.lastIndexOf(",");
			if(lastIndex != -1) {
				sb.deleteCharAt(sb.lastIndexOf(","));
			}
			out.println(sb);
			out.println();

			// Next Semester
			requiredCourses.removeAll(coursesForSemester);
			semester++;
		}
	}

	private String getLectureSection(String labSection) {
		int indexOfSpace = labSection.indexOf(' ');
		return labSection.substring(0, indexOfSpace + 2) + "3" + labSection.substring(indexOfSpace + 3);
	}

	private void getStartingOptions() {
		// Construct objects
		compareCourse = (Course c1, Course c2) -> {
			// Sort first by being a parent of a co-requisite
			boolean c1CoParent = false, c2CoParent = false;
			if(this.coParents != null) {
				c1CoParent = this.coParents.contains(c1);
				c2CoParent = this.coParents.contains(c2);
			}
			if(!(c1CoParent ^ c2CoParent)) {
				// Then sort on being a child of a co-requisite
				boolean c1CoChild = false, c2CoChild = false;
				if(this.courseCo != null) {
					c1CoChild = this.courseCo.containsKey(c1);
					c2CoChild = this.courseCo.containsKey(c2);
				}
				if(!(c1CoChild ^ c2CoChild)) {
					// Then sort on being a parent of a prerequisite
					boolean c1PreParent = false, c2PreParent = false;
					if(this.preParents != null) {
						c1PreParent = this.preParents.contains(c1);
						c2PreParent = this.preParents.contains(c2);
					}
					if(!(c1PreParent ^ c2PreParent)) {
						// Then sort on the other rules
						return c1.compareTo(c2);
					} else {
						return c1PreParent ? -1 : 1;
					}
				} else {
					return c1CoChild ? -1 : 1;
				}
			} else {
				return c1CoParent ? -1 : 1;
			}
		};

		TreeSet<Course> test = new TreeSet<Course>(compareCourse);

		coursesAbleToTake = new PriorityQueue<Course>(compareCourse);

		// Add the courses that can currently be taken
		for(Course c : preDegreeIn.keySet()) {
			if(preDegreeIn.get(c) == 0 && coDegreeIn.get(c) == 0) {
				coursesAbleToTake.add(c);
				test.add(c);
			}
		}
	}

	private void handleExistingCredit() {
		// Construct object
		TreeSet<Course> coursesBypassed = new TreeSet<Course>();

		if(bypass != null) {
			// Determine which courses are being bypassed.
			bypass.stream().forEach(listOfOptions -> {
				if(listOfOptions.size() == 1) {
					listOfOptions.get(0).stream().filter(course -> requiredCourses.contains(course)).forEach(course -> coursesBypassed.add(course));
				} else {
					// Determine which option best aligns with the student's required courses
					int bestOptionIndex = 0;
					int bestOptionScore = 0;
					for(int i = 0; i < listOfOptions.size(); i++) {
						int currOptionScore = listOfOptions.get(i).stream().filter(course -> requiredCourses.contains(course)).collect(Collectors.toSet()).size();
						if(currOptionScore > bestOptionScore) {
							bestOptionIndex = i;
							bestOptionScore = currOptionScore;
						}
					}
					listOfOptions.get(bestOptionIndex).stream().filter(course -> requiredCourses.contains(course)).forEach(course -> coursesBypassed.add(course));
				}
			});

			// These courses no longer need to be completed
			requiredCourses.removeAll(coursesBypassed);

			// For all of the courses bypassed...
			coursesBypassed.stream().forEach(course -> {
				// Get the courses that they connect to
				TreeSet<Course> children = graph.get(course);
				// If it connects to a course...
				if(children.size() != 0) {
					// Deduct one from that course's in-degree
					children.stream().forEach(childCourse -> {
						// If the relationship is that the parent is a prerequisite for the child...
						if(coursePre.get(childCourse).contains(course)) {
							// Deduct one from the in-degree of prerequisites for the child
							preDegreeIn.put(childCourse, preDegreeIn.get(childCourse)-1);
						}
						// Else, if the relationship that the parent is a co-requisite for the child...
						else if(courseCo.get(childCourse).contains(course)) {
							// Deduct one from the in-degree of co-requisites for the child
							coDegreeIn.put(childCourse, coDegreeIn.get(childCourse)-1);
						}
					});
				}
				// Artificially set the in-degree of the course bypassed to -1 (special number)
				preDegreeIn.put(course, -1);
				coDegreeIn.put(course, -1);
			});

			// Print the courses that are being bypassed 
			StringBuilder sb = new StringBuilder();
			coursesBypassed.stream().forEach(course -> {
				sb.append(course);
				sb.append(", ");
			});
			int index = sb.lastIndexOf(",");
			if(index != -1) {
				sb.deleteCharAt(index);
			}
			out.println("Pre-existing Credit - " + coursesBypassed.stream().map(course -> course.creditHours).mapToInt(Integer::intValue).sum() + " Credit Hours - " + coursesBypassed.size() + " Courses:");
			out.println(sb);
			out.println();
		} else {
			out.println("Existing Credit - 0 Credit Hours - 0 Courses:\nNone\n");
		}
	}

	private void topologicalSortInit() {
		// Construct objects
		coDegreeIn = new TreeMap<Course, Integer>();
		preDegreeIn = new TreeMap<Course, Integer>();
		graph = new TreeMap<Course, TreeSet<Course>>();

		// Initially add all courses to the graph
		requiredCourses.stream().forEach(course -> graph.put(course, new TreeSet<Course>()));

		// Initially construct the in-degree map
		requiredCourses.stream().forEach(course -> {
			coDegreeIn.put(course, 0);
			preDegreeIn.put(course, 0);
		});

		// Add the in-degree for prerequisites and add the edges in the graph
		if(coursePre != null) {
			for(Course parent : coursePre.keySet()) {
				// In-degree
				preDegreeIn.put(parent, coursePre.get(parent).size());

				// Graph
				TreeSet<Course> owningChildren = coursePre.get(parent);
				owningChildren.stream().forEach(child -> {
					TreeSet<Course> ownedChildren = graph.get(child);
					ownedChildren.add(parent);
					graph.put(child, ownedChildren);
				});
			}
		}


		// Add the in-degree for co-requisites and add the edges in the graph
		if(courseCo != null) {
			for(Course parent : courseCo.keySet()) {
				// In-degree
				coDegreeIn.put(parent, courseCo.get(parent).size());

				// Graph
				TreeSet<Course> owningChildren = courseCo.get(parent);
				owningChildren.stream().forEach(child -> {
					TreeSet<Course> ownedChildren = graph.get(child);
					ownedChildren.add(parent);
					graph.put(child, ownedChildren);
				});
			}
		}
	}

	public void readPreCorequisites() throws IOException  {
		// Take in the pre-requisites and co-requisites
		coursePre = new TreeMap<Course, TreeSet<Course>>();
		courseCo = new TreeMap<Course, TreeSet<Course>>();
		preParents = new TreeSet<Course>();
		coParents = new TreeSet<Course>();
		String line = file.readLine();

		// If there are pre-requisites and co-requisites...
		if(!line.equals("null")) {
			String[] relationshipsCombined = line.split(";");
			String[][] relationships = Arrays.asList(relationshipsCombined).stream().map(str -> str.split(",")).collect(Collectors.toList()).toArray(new String[relationshipsCombined.length][3]);
			Arrays.asList(relationships).stream().forEach((arr) -> {
				Course parent = new Course(arr[0]);
				Course child = new Course(arr[2]);
				if(arr[1].equals("P")) {
					TreeSet<Course> prerequisites = (coursePre.containsKey(child)) ? (coursePre.get(child)) : (new TreeSet<Course>());
					prerequisites.add(parent);
					coursePre.put(child, prerequisites);
					preParents.add(parent);
				} else {
					TreeSet<Course> corequisites = (courseCo.containsKey(child)) ? (courseCo.get(child)) : (new TreeSet<Course>());
					corequisites.add(parent);
					courseCo.put(child, corequisites);
					coParents.add(parent);
				}
			});
		} else {
			coursePre = null;
			courseCo = null;
		}
	}

	public void readAPScores() throws IOException {
		// Take in the list of AP Scores
		String line = file.readLine();
		APTest[] apTestAndScore;
		if(!line.equals("null")) {
			String[] apTests = line.split(";");
			String[][] scores = Arrays.asList(apTests).stream().map(str -> str.split(",")).collect(Collectors.toList()).toArray(new String[apTests.length][2]);
			apTestAndScore = Arrays.asList(scores).stream().map(arr -> new APTest(arr[0], Integer.parseInt(arr[1]))).collect(Collectors.toList()).toArray(new APTest[apTests.length]);
		} else {
			apTestAndScore = null;
		}

		// Convert the AP Scores 
		if(apTestAndScore != null) {
			bypass = new ArrayList<ArrayList<TreeSet<Course>>>();
			for(APTest score : apTestAndScore) {
				bypass.add(testToCourses.get(score));
			}
		} else {
			bypass = null;
		}
	}

	public void initTestToCourses() {
		testToCourses = new TreeMap<APTest, ArrayList<TreeSet<Course>>>();

		// Shared Cases
		this.generateSameAcross45Single();
		this.generateSameAcross45Double();
		this.generateSameAcrossAllSingle();
		this.generateLanguage123();

		// Unique Cases
		this.generateComputerScience();
		this.generateChemistry();
		this.generateChinese();
		this.generateUSHistory();
		this.generateEnv();
		this.generateCalculus();
		this.generateSpanish();
	}

	private void generateSameAcross45Single() {
		ArrayList<TreeSet<Course>> outer;
		TreeSet<Course> inner;
		String[] courseIDs = {"AHST 2331", "ARTS 2380", "ARTS 1316", 
				"ECON 2301", "ECON 2302", "RHET 1302", 
				"LIT 2331", "PSY 2301", "STAT 1342"};
		String[] courses = {"History of Art", "Studio Art: 2-D Design", "Studio Art: Drawing", 
				"Macroeconomics", "Microeconomics", "English Language and Composition",
				"English Literature and Composition", "Psychology", "Statistics"};
		for(int i = 0; i < courseIDs.length; i++) {
			outer = new ArrayList<TreeSet<Course>>();
			inner = new TreeSet<Course>();
			inner.add(new Course(courseIDs[i]));
			outer.add(inner);
			testToCourses.put(new APTest(courses[i], 4), outer);
			testToCourses.put(new APTest(courses[i], 5), outer);
		}
	}

	private void generateSameAcross45Double() {
		ArrayList<TreeSet<Course>> outer;
		TreeSet<Course> inner;
		String[][] courseIDs = {{"PHYS 1301", "PHYS 1101"}, {"PHYS 1302", "PHYS 1102"}, {"PHYS 2325", "PHYS 2125"}, {"PHYS 2326", "PHYS 2126"}};
		String[] courses = {"Physics 1", "Physics 2", "Physics C: Mechanics", "Physics C: Electrical and Magnetic"};
		for(int i = 0; i < courseIDs.length; i++) {
			outer = new ArrayList<TreeSet<Course>>();
			inner = new TreeSet<Course>();
			inner.add(new Course(courseIDs[i][0]));
			inner.add(new Course(courseIDs[i][1]));
			outer.add(inner);
			testToCourses.put(new APTest(courses[i], 4), outer);
			testToCourses.put(new APTest(courses[i], 5), outer);
		}
	}

	private void generateSameAcrossAllSingle() {
		ArrayList<TreeSet<Course>> outer;
		TreeSet<Course> inner;
		String[] courseIDs = {"GEOG 2303", "GOVT 2305", "MUSI 2328"};
		String[] courses = {"Human Geography", "United States Government and Politics", "Music Theory"};
		for(int i = 0; i < courseIDs.length; i++) {
			outer = new ArrayList<TreeSet<Course>>();
			inner = new TreeSet<Course>();
			inner.add(new Course(courseIDs[i]));
			outer.add(inner);
			testToCourses.put(new APTest(courses[i], 3), outer);
			testToCourses.put(new APTest(courses[i], 4), outer);
			testToCourses.put(new APTest(courses[i], 5), outer);
		}
	}

	private void generateLanguage123() {
		ArrayList<TreeSet<Course>> outer;
		TreeSet<Course> inner;
		Course course1, course2, course3;
		String[][] courseIDs = {{"GERM 1311", "GERM 1312", "GERM 2311"}, 
				{"FREN 1311", "FREN 1312", "FREN 2311"}, 
				{"JAPN 1311", "JAPN 1312", "JAPN 2311"}};
		String[] courses = {"German Language", "French Language", "Japanese Language & Culture"};
		for(int i = 0; i < courseIDs.length; i++) {
			course1 = new Course(courseIDs[i][0]);
			course2 = new Course(courseIDs[i][1]);
			course3 = new Course(courseIDs[i][2]);

			outer = new ArrayList<TreeSet<Course>>();
			inner = new TreeSet<Course>();
			inner.add(course1);
			outer.add(inner);
			testToCourses.put(new APTest(courses[i], 3), outer);

			outer = new ArrayList<TreeSet<Course>>();
			inner = new TreeSet<Course>(inner);
			inner.add(course2);
			outer.add(inner);
			testToCourses.put(new APTest(courses[i], 4), outer);

			outer = new ArrayList<TreeSet<Course>>();
			inner = new TreeSet<Course>(inner);
			inner.add(course3);
			outer.add(inner);
			testToCourses.put(new APTest(courses[i], 5), outer);
		}
	}

	private void generateComputerScience() {
		ArrayList<TreeSet<Course>> outer;
		TreeSet<Course> inner;
		Course course1, course2, course3;

		// Computer Science A - 4
		outer = new ArrayList<TreeSet<Course>>();
		inner = new TreeSet<Course>();
		course1 = new Course("CS 1336");
		course2 = new Course("CS 1136");
		course3 = new Course("CS 1337");
		inner.add(course1);
		inner.add(course2);
		inner.add(course3);
		outer.add(inner);
		testToCourses.put(new APTest("Computer Science A", 4), outer);

		// Computer Science A - 5
		testToCourses.put(new APTest("Computer Science A", 5), outer);
	}

	private void generateChemistry() {
		ArrayList<TreeSet<Course>> outer;
		TreeSet<Course> inner;
		Course course1, course2, course3, course4;

		// General Chemistry - 4
		outer = new ArrayList<TreeSet<Course>>();
		inner = new TreeSet<Course>();
		course1 = new Course("CHEM 1311");
		course2 = new Course("CHEM 1111");
		inner.add(course1);
		inner.add(course2);
		outer.add(inner);
		testToCourses.put(new APTest("General Chemistry", 4), outer);

		// General Chemistry - 5
		outer = new ArrayList<TreeSet<Course>>();
		inner = new TreeSet<Course>(inner);
		course3 = new Course("CHEM 1312");
		course4 = new Course("CHEM 1112");
		inner.add(course3);
		inner.add(course4);
		outer.add(inner);
		testToCourses.put(new APTest("General Chemistry", 5), outer);
	}

	private void generateChinese() {
		ArrayList<TreeSet<Course>> outer;
		TreeSet<Course> inner;
		Course course1, course2;

		// Chinese Language & Culture - 3
		outer = new ArrayList<TreeSet<Course>>();
		inner = new TreeSet<Course>();
		course1 = new Course("CHIN 1311");
		inner.add(course1);
		outer.add(inner);
		testToCourses.put(new APTest("Chinese Language & Culture", 3), outer);

		// Chinese Language & Culture - 4
		outer = new ArrayList<TreeSet<Course>>();
		inner = new TreeSet<Course>(inner);
		course2 = new Course("CHIN 1312");
		inner.add(course2);
		outer.add(inner);
		testToCourses.put(new APTest("Chinese Language & Culture", 4), outer);

		// Chinese Language & Culture - 5
		testToCourses.put(new APTest("Chinese Language & Culture", 5), outer);
	}

	private void generateUSHistory() {
		ArrayList<TreeSet<Course>> outer;
		TreeSet<Course> inner;
		Course course1, course2;

		// United States History - 3
		outer = new ArrayList<TreeSet<Course>>();
		inner = new TreeSet<Course>();
		course1 = new Course("HIST 1301");
		inner.add(course1);
		outer.add(inner);
		testToCourses.put(new APTest("United States History", 3), outer);

		// United States History - 4
		outer = new ArrayList<TreeSet<Course>>();
		inner = new TreeSet<Course>(inner);
		course2 = new Course("HIST 1302");
		inner.add(course2);
		outer.add(inner);
		testToCourses.put(new APTest("United States History", 4), outer);

		// United States History - 5
		testToCourses.put(new APTest("United States History", 5), outer);
	}

	private void generateEnv() {
		ArrayList<TreeSet<Course>> outer;
		TreeSet<Course> inner1, inner2, inner3;
		Course course1, course2, course3;

		// Environmental Science - 3
		outer = new ArrayList<TreeSet<Course>>();
		inner1 = new TreeSet<Course>();
		inner2 = new TreeSet<Course>();
		inner3 = new TreeSet<Course>();
		course1 = new Course("GEOS 2302");
		course2 = new Course("ENVR 2302");
		course3 = new Course("GEOG 2302");
		inner1.add(course1);
		inner2.add(course2);
		inner3.add(course3);
		outer.add(inner1);
		outer.add(inner2);
		outer.add(inner3);
		testToCourses.put(new APTest("Environmental Science", 3), outer);

		// Environmental Science - 4
		testToCourses.put(new APTest("Environmental Science", 4), outer);

		// Environmental Science - 5
		testToCourses.put(new APTest("Environmental Science", 5), outer);
	}

	private void generateCalculus() {
		ArrayList<TreeSet<Course>> outer;
		TreeSet<Course> inner1, inner2, inner3;
		Course course1, course2, course3, course4, course5, course6;

		// Calculus AB - 3
		outer = new ArrayList<TreeSet<Course>>();
		inner1 = new TreeSet<Course>();
		course1 = new Course("MATH 2312");
		inner1.add(course1);
		outer.add(inner1);
		testToCourses.put(new APTest("Calculus AB", 3), outer);

		// Calculus AB - 4
		outer = new ArrayList<TreeSet<Course>>();
		inner1 = new TreeSet<Course>(inner1);
		inner2 = new TreeSet<Course>(inner1);
		course2 = new Course("MATH 1325");
		course3 = new Course("MATH 2413");
		inner1.add(course2);
		inner2.add(course3);
		outer.add(inner1);
		outer.add(inner2);
		testToCourses.put(new APTest("Calculus AB", 4), outer);

		// Calculus AB - 5
		testToCourses.put(new APTest("Calculus AB", 5), outer);

		// Calculus BC - 3
		testToCourses.put(new APTest("Calculus BC", 3), outer);

		// Calculus BC - 4
		outer = new ArrayList<TreeSet<Course>>();
		inner1 = new TreeSet<Course>();
		inner2 = new TreeSet<Course>();
		inner3 = new TreeSet<Course>();
		course4 = new Course("MATH 2417");
		course5 = new Course("MATH 1326");
		course6 = new Course("MATH 2414");
		inner1.add(course1);
		inner1.add(course4);
		inner2.add(course2);
		inner2.add(course5);
		inner3.add(course3);
		inner3.add(course6);
		outer.add(inner1);
		outer.add(inner2);
		outer.add(inner3);
		testToCourses.put(new APTest("Calculus BC", 4), outer);

		// Calculus BC - 5
		testToCourses.put(new APTest("Calculus BC", 5), outer);
	}

	private void generateSpanish() {
		ArrayList<TreeSet<Course>> outer;
		TreeSet<Course> inner;
		Course course1, course2, course3, course4;

		// Spanish Language & Culture - 3
		outer = new ArrayList<TreeSet<Course>>();
		inner = new TreeSet<Course>();
		course1 = new Course("SPAN 1311");
		course2 = new Course("SPAN 1312");
		inner.add(course1);
		inner.add(course2);
		outer.add(inner);
		testToCourses.put(new APTest("Spanish Language & Culture", 3), outer);

		// Spanish Language & Culture - 4
		outer = new ArrayList<TreeSet<Course>>();
		inner = new TreeSet<Course>(inner);
		course3 = new Course("SPAN 2311");
		inner.add(course3);
		outer.add(inner);
		testToCourses.put(new APTest("Spanish Language & Culture", 4), outer);

		// Spanish Literature & Culture - 3
		testToCourses.put(new APTest("Spanish Literature & Culture", 3), outer);

		// Spanish Language & Culture - 5
		outer = new ArrayList<TreeSet<Course>>();
		inner = new TreeSet<Course>(inner);
		course4 = new Course("SPAN 2312");
		inner.add(course4);
		outer.add(inner);
		testToCourses.put(new APTest("Spanish Language & Culture", 5), outer);

		// Spanish Literature & Culture - 4
		testToCourses.put(new APTest("Spanish Literature & Culture", 4), outer);

		// Spanish Literature & Culture - 5
		testToCourses.put(new APTest("Spanish Literature & Culture", 5), outer);
	}
}