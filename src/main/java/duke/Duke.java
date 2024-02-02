package duke;
import exceptions.DukeException;
import exceptions.TaskNotExistException;
import model.Deadline;
import model.Event;
import model.Task;
import model.Todo;
import service.Parser;
import service.Storage;
import service.TaskList;
import ui.UI;


public class Duke{
    
    public enum Command {
        //TODO, DEADLINE, EVENT, DELETE, MARK, UNMARK, LIST, BYE, UNKNOWN;
        Todo, Deadline, Event, Delete, Mark, Unmark, List, Bye, Unknown;
        public static Command fromString(String maybeCommand) {
            try {
                return Command.valueOf(maybeCommand.toUpperCase());
            } catch (Exception e) {
                return Unknown;
            }
        }
    }

    /**
     * Performs the appropriate action based on the user's input of the task.
     * From adding of tasks which can be todos, deadlines and events, deleting and marking and unmarking them.
     *
     * @param userInput user's commands
     * @param todos the list of tasks that are currently present.
     * @param verbose helps to ensure whether we are preloading (includes the need to save the entries)
     * @throws RuntimeException
     */
    public static void processCommand(String userInput, TaskList todos, Boolean verbose) throws RuntimeException {
        String maybeCommand;
        String arr[];
        try {
            arr = userInput.split(" ", 2); // String in Array format. Useful: https://www.geeksforgeeks.org/split-string-java-examples/
            maybeCommand = arr[0];
        } catch (Exception e) {
            maybeCommand = null;
            return;
        }

        Command command = Command.fromString(maybeCommand);

        switch (command) {
        case Bye:
            ui.formalities("farewell");
            System.exit(0);
            break; //TODO uncessary?
        case List:
            // list tasks
            if (verbose) {
                ui.showLine();
                System.out.println(" Here are the tasks in your list:");
            }
            for (int i = 0; i < todos.size(); i++) {
                Task currTask = todos.get(i);
                if (verbose) { System.out.println((i + 1) + ". " + currTask.toString()); }
            }
            if (verbose) {
                ui.showLine();
            }
            break;
        case Mark:
            // mark task as done
            Integer index = Integer.valueOf(arr[1]) - 1;
            Task currTask = todos.get(index);
            currTask.markAsDone();
            if (verbose) {
                System.out.print(" Nice! I've marked this task as done:\n");
                System.out.println(" " + currTask.toString());
                ui.showLine();
                storage.updateRecords(taskList);
            }
            break;
        case Unmark:
            // mark task as undone
            index = Integer.valueOf(arr[1]) - 1;
            if (verbose) {
                ui.showLine();
                System.out.print(" OK, I've marked this task as not done yet:\n");
            }

            currTask = todos.get(index);
            currTask.markAsUndone();

            if (verbose) {
                System.out.println(" " + currTask.toString());
                ui.showLine();
                storage.updateRecords(taskList);
            }
            break;
        case Todo:
            if (arr.length == 1) {
                throw new DukeException(" Nuh uh! The description of a todo cannot be empty.");
            }
            if (verbose) {
                ui.showLine();
                System.out.println("Got it. Added this task:");
            }
            Todo todo = new Todo(arr[1]);
            todos.add(todo);
            if (verbose) {
                System.out.println(todo.toString());
                System.out.println("Now you have " + todos.size() + " tasks in the list.");
                ui.showLine();
                storage.updateRecords(taskList);
            }
            break;
        case Deadline:
            if (arr.length == 1) {
                throw new DukeException(" Nuh uh! The description of a deadline cannot be empty.\nMake sure to add a deadline after the description with /by too!");
            }
            if (verbose) {
                ui.showLine();
                System.out.println("Got it. Added this task:");
            }
            String arguments[] = arr[1].split(" /by ");
            String description = arguments[0];
            String by = arguments[1];
            Deadline deadline = new Deadline(description, by);
            todos.add(deadline);
            if (verbose) {
                System.out.println(deadline.toString());
                System.out.println("Now you have " + todos.size() + " tasks in the list.");
                ui.showLine();
                storage.updateRecords(taskList);
            }
            break;
        case Event:
            if (arr.length == 4) {
                throw new DukeException(" Nuh uh! The description of an event cannot be empty.\nMake sure to add a from and to date after the description with /from and /to too!");
            }
            if (verbose) {
                ui.showLine();
                System.out.println("Got it. Added this task:");
            }
            // extraction of parameters
            String getDesc[] = arr[1].split(" /from ");
            String desc = getDesc[0];
            String getDates[] = getDesc[1].split(" /to ");
            String from = getDates[0];
            String to = getDates[1];

            //creating of event
            Event event = new Event(desc, from, to);
            todos.add(event);

            if (verbose) {
                System.out.println(event.toString());
                System.out.println("Now you have " + todos.size() + " tasks in the list.");
                ui.showLine();
                storage.updateRecords(taskList);
            }
            break;
        case Delete:
            if (arr.length == 1) {
                throw new DukeException(" Nuh uh! Which task to delete? \nMake sure to add the task number!");
            }
            if (verbose) {
                ui.showLine();
            }
            Integer i = Integer.valueOf(arr[1]);
            Task task;
            try {
                task = todos.get(i - 1);
                todos.remove(i - 1);
            } catch (Exception e) {
                throw new TaskNotExistException(Integer.toString(i));
            }
            if (verbose) {
                System.out.println("Okay! I've fed this task to Woodstock, bye bye!:");
                System.out.println(task.toString());
                System.out.println("Now you have " + todos.size() + " tasks in the list.");
                ui.showLine();
                storage.updateRecords(taskList);
            }
            break;
        default:
            ui.showLine();
            System.out.println("Uh ah I don't understand ya ");
            ui.showLine();
            break;
        }
    }

    private static TaskList taskList;
    //private service.TaskList tasks;
    private static UI ui;
    private static Storage storage;

    /**
     * Constructor of Duke
     * @param filePath file storage location to save and retrieve list of tasks
     */
    public Duke(String filePath) {
        ui = new UI();
        taskList = new TaskList();
        storage = new Storage(filePath);
    }


    /**
     * With a Duke object, .run() is the main entry point of the program, running all its processes.
     *
     */
    public void run() {
        //do something
        ui.formalities("greet");

        //Load existing information
        try {
            storage.loadInfo(taskList);
            ui.showLine();
        } catch (Exception e) {
            e.getMessage();
            taskList = null;
        }

        while (true) {
            //Parsing user input.
           String command = new Parser().parse();

            //Process user command
            try {
                processCommand(command, taskList, true);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                ui.showLine();
            }
        }
    }

    public static void main(String[] args) {
        new Duke("./src/main/java/data/duke.txt").run();
    }
}
