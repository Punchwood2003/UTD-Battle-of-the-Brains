package course_planning;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.TreeMap;
import java.util.TreeSet;

public class CoursePlanning {
	class Course implements Comparable<Course> {
		public String subject, id;
		public int year, creditHours, uniqueID;

		public Course(String subject, String courseNumber) {
			this.subject = subject;
			this.year = courseNumber.charAt(0) - '0';
			this.creditHours = courseNumber.charAt(1) - '0';
			this.id = courseNumber.substring(2);
			this.uniqueID = Integer.parseInt(this.id);
		}

		public Course(String courseID) {
			int indexOfSpace = courseID.indexOf(' ');
			this.subject = courseID.substring(0, indexOfSpace);
			this.year = courseID.charAt(indexOfSpace + 1) - '0';
			this.creditHours = courseID.charAt(indexOfSpace + 2) - '0';
			this.id = courseID.substring(indexOfSpace + 3);
			this.uniqueID = Integer.parseInt(this.id);
		}

		public String getCourseID() {
			return this.subject + " " + year + creditHours + id;
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
			return this.getCourseID();
		}
	}

	public BufferedReader file;
	public PrintWriter out;
	public TreeMap<Course, TreeSet<Course>> coursePre, courseCo, graph;
	public TreeMap<Course, Integer> coDegreeIn, preDegreeIn;
	public PriorityQueue<Course> coursesAbleToTake;
	public TreeSet<Course> requiredCourses, bypass, preParents, coParents;
	public ArrayList<Course> updateAfterSemester;
	public Comparator<Course> compareCourse;

	public static void main(String[] args) throws IOException {
		new CoursePlanning().run();
	}

	public void run() throws IOException {
		// Create I/O objects
		file = new BufferedReader(new InputStreamReader(System.in));
		out = new PrintWriter(System.out);

		// Take in the major
		String major = file.readLine();

		// Take in the list of required courses
		requiredCourses = new TreeSet<Course>();
		String[] courseIDs = file.readLine().split(" ");
		for(int i = 0; i < courseIDs.length; i += 2) {
			requiredCourses.add(new Course(courseIDs[i], courseIDs[i+1]));
		}

		// Take in the pre-existing credit
		String line = file.readLine();
		if(!line.equals("null")) {
			bypass = new TreeSet<Course>();
			courseIDs = line.split(" ");
			for(int i = 0; i < courseIDs.length; i += 2) {
				bypass.add(new Course(courseIDs[i], courseIDs[i+1]));
			}
		} else {
			bypass = null;
		}

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
						Course lecture = new Course(this.getLectureSection(curr.getCourseID()));
						// If the course has a lab period (and isn't itself the lab period)...
						if(requiredCourses.contains(lecture) && !curr.getCourseID().equals(lecture.getCourseID())) {
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
				if(degree == 0 && coDegreeIn.containsKey(child) && coDegreeIn.get(child) == 0) {
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
		if(bypass != null) {
			// These courses no longer need to be completed
			requiredCourses.removeAll(bypass);

			// For all of the courses bypassed...
			bypass.stream().forEach(course -> {
				// Get the courses that they connect to
				TreeSet<Course> children = graph.get(course);
				// If it connects to a course...
				if(children.size() != 0) {
					// Deduct one from that course's in-degree
					children.stream().forEach(childCourse -> {
						// If the relationship is that the parent is a prerequisite for the child...
						if(coursePre.containsKey(childCourse) && coursePre.get(childCourse).contains(course)) {
							// Deduct one from the in-degree of prerequisites for the child
							preDegreeIn.put(childCourse, preDegreeIn.get(childCourse)-1);
						}
						// Else, if the relationship that the parent is a co-requisite for the child...
						else if(courseCo.containsKey(childCourse) && courseCo.get(childCourse).contains(course)) {
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
			bypass.stream().forEach(course -> {
				sb.append(course);
				sb.append(", ");
			});
			int index = sb.lastIndexOf(",");
			if(index != -1) {
				sb.deleteCharAt(index);
			}
			out.println("Pre-existing Credit - " + bypass.stream().map(course -> course.creditHours).mapToInt(Integer::intValue).sum() + " Credit Hours - " + bypass.size() + " Courses:");
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
		String line = file.readLine();

		// If there are pre-requisites and co-requisites...
		if(!line.equals("null")) {
			// Create Objects
			coursePre = new TreeMap<Course, TreeSet<Course>>();
			courseCo = new TreeMap<Course, TreeSet<Course>>();
			preParents = new TreeSet<Course>();
			coParents = new TreeSet<Course>();
			
			while(line != null) {
				String[] relationships = line.split(" ");
				Course child = new Course(relationships[0], relationships[1]);
				for(int i = 3; i < relationships.length; i += 3) {
					Course parent = new Course(relationships[i + 1], relationships[i + 2]);
					if(relationships[i].equals("P")) {
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
				}
				line = file.readLine();
			}
		} else {
			coursePre = null;
			courseCo = null;
			preParents = null;
			coParents = null;
		}
	}
}